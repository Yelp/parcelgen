#!/usr/bin/env python

import sys, re, os.path, json

# Parcelgen generates parcelable Java classes based
# on a json dictionary of types and properties.  It generates
# a class with the appropriate members and working
# writeToParcel and readFromParcel methods.

# Primary Author: Alex Pretzlav <pretz@yelp.com>

class ParcelGen:
    BASE_IMPORTS = ("android.os.Parcel", "android.os.Parcelable")
    CLASS_STR = "/* package */abstract class %s implements %s {"
    CHILD_CLASS_STR = "public class {0} extends _{0} {{"
    NATIVE_TYPES = ["string", "byte", "double", "float", "int", "long"]
    BOX_TYPES = ["Byte", "Boolean", "Float", "Integer", "Long", "Short", "Double"]
    JSON_IMPORTS = ["org.json.JSONException", "org.json.JSONObject"]
    TAB_STR = "    "

    tablevel = 0
    outfile = None

    def tabify(self, string):
        return (self.TAB_STR * self.tablevel) + string

    def printtab(self, string):
        self.output(self.tabify(string))

    def newline(self, count=1):
        self.output("\n" * (count-1))

    def output(self, string=""):
        if self.outfile:
            self.outfile.write(string + "\n")
        else:
            print string

    def uptab(self):
        self.tablevel += 1

    def downtab(self):
        self.tablevel -= 1

    def memberize(self, name):
        return "m%s%s" % (name[0].capitalize(), name[1:])

    def member_map(self):
        for typ in self.get_types():
            for member in self.props[typ]:
                yield (typ, member)

    def gen_getter(self, typ, member):
        method_name = ""
        if typ == "boolean" and member.startswith("is"):
            method_name = member
        else:
            method_name = "get%s%s" % (member[0].capitalize(), member[1:])
        return "%spublic %s %s() {\n%s%sreturn %s;\n%s}" % (self.TAB_STR, typ, method_name, self.TAB_STR, self.TAB_STR, self.memberize(member), self.TAB_STR)

    def list_type(self, typ):
        match = re.match(r"(List|ArrayList)<(.*)>", typ)
        if match:
            return match.group(2)
        return None
    
    def array_type(self, typ):
        match = re.match(r"(.*)(\[\])", typ)
        if match:
            return match.group(1).strip()
        return None

    def gen_list_parcelable(self, typ, memberized):
        classname = self.list_type(typ)
        if not classname:
            return None
        elif classname == "String":
            return self.tabify("parcel.writeStringList(%s);" % memberized)
        else:
            return self.tabify("parcel.writeTypedList(%s);" % memberized)

    def gen_list_unparcel(self, typ, memberized):
        classname = self.list_type(typ)
        if not classname:
            return None
        if (classname == "String"):
            return self.tabify("%s = source.createStringArrayList();" % memberized)
        else:
            return self.tabify("%s = source.createTypedArrayList(%s.CREATOR);" % (memberized, classname))

    def gen_array_parcelable(self, typ, memberized):
        classname = self.array_type(typ)
        if not classname:
            return None
        elif classname.lower() in self.NATIVE_TYPES + ["boolean"]:
            return self.tabify("parcel.write%sArray(%s);" % (classname.capitalize(), memberized))
        else:
            return self.tabify("parcel.writeTypedArray(%s, 0);" % memberized)

    def gen_array_unparcel(self, typ, memberized):
        classname = self.array_type(typ)
        if not classname:
            return None
        elif classname.lower() in self.NATIVE_TYPES + ["boolean"]:
            assignment = self.tabify("%s = source.create%sArray();\n" % (memberized, classname.capitalize())) 			
            return assignment 
        else:
            assignment = self.tabify("%s = source.createTypedArray(%s.CREATOR);\n" % (memberized, classname.capitalize()))
            return assignment 

    def gen_parcelable_line(self, typ, member):
        memberized = self.memberize(member)
        if typ in self.BOX_TYPES:
            return self.tabify("parcel.writeValue(%s);" % (memberized))
        elif typ.lower() in self.NATIVE_TYPES:
            return self.tabify("parcel.write%s(%s);" % (typ.capitalize(), memberized))
        elif typ == "Date":
            return self.tabify("parcel.writeLong(%s == null ? Integer.MIN_VALUE : %s.getTime());" % (
                memberized, memberized))
        elif self.array_type(typ):
            return self.gen_array_parcelable(typ, memberized)
        elif self.list_type(typ):
            return self.gen_list_parcelable(typ, memberized)
        elif typ in self.serializables:
            return self.tabify("parcel.writeSerializable(%s);" % memberized)
        else:
            return self.tabify("parcel.writeParcelable(%s, 0);" % memberized)

    def get_types(self):
        types = self.props.keys()
        types.sort()
        return types

    def gen_parcelable(self):
        result = ""
        for typ in self.get_types():
            if typ == "boolean":
                joined = ", ".join(map(self.memberize, self.props[typ]))
                result += self.tabify("parcel.writeBooleanArray(new boolean[] {%s});\n" % joined)
            else:
                for member in self.props[typ]:
                    result += self.gen_parcelable_line(typ, member) + "\n"
        return result[:-1] # Strip last newline because I'm too lazy to do this right

    def print_creator(self, class_name, parcel_class, close=True):
        # Simple parcelable creator that uses readFromParcel
        self.printtab("public static final {0}<{1}> CREATOR = new {0}<{1}>() {{".format(
                 parcel_class, class_name))
        self.uptab()
        self.newline()
        self.printtab(("public {0}[] newArray(int size) {{\n{1}return new {0}[size];\n%s%s}}" % (self.TAB_STR, self.TAB_STR)).format(
            class_name, self.TAB_STR * (self.tablevel + 1)))
        self.newline()
        self.printtab("public %s createFromParcel(Parcel source) {" % class_name)
        self.uptab()
        self.printtab("{0} object = new {0}();".format(class_name))
        self.printtab("object.readFromParcel(source);")
        self.printtab("return object;")
        self.downtab()
        self.printtab("}")
        if close:
            self.downtab()
            self.printtab("};\n")
            self.downtab()

    def print_child(self, child_name, package):
        self.tablevel = 0
        self.printtab("package %s;\n" % package)
        imports = ["android.os.Parcel"]
        if self.do_json:
            imports.extend(self.JSON_IMPORTS)
            imports.append("com.yelp.parcelgen.JsonParser.DualCreator")
        else:
            imports.append("android.os.Parcelable")
        for import_string in imports:
            self.printtab("import %s;" % import_string)
        self.newline(2)
        self.printtab(self.CHILD_CLASS_STR.format(child_name))
        self.newline()
        self.uptab()
        if self.do_json:
            self.print_creator(child_name, "DualCreator", False)
            self.newline()
            self.printtab("@Override")
            self.printtab("public %s parse(JSONObject obj) throws JSONException {" % child_name)
            self.uptab()
            self.printtab("{0} newInstance = new {0}();".format(child_name))
            self.printtab("newInstance.readFromJson(obj);")
            self.printtab("return newInstance;")
            self.downtab()
            self.printtab("}\n    };\n")
            self.downtab()
        else:
            self.print_creator(child_name, "Parcelable.Creator")
        self.downtab()
        self.printtab("}")

    def needs_jsonutil(self):
        if "Date" in self.props:
            return True
        for key in self.props.keys():
            if "List" in key:
                return True
        return False

    def needs_jsonarray(self):
        if any("[]" in s for s in self.props.keys()):
            return True

    def print_gen(self, props, class_name, package, imports, transient):
        self.props = props
        self.tablevel = 0
        # Imports and open class definition
        self.printtab("package %s;\n" % package)
        imports = set(tuple(imports) + self.BASE_IMPORTS)
        for prop in props.keys():
            if prop.startswith("List"):
                imports.add("java.util.List")
            elif prop.startswith("ArrayList"):
                imports.add("java.util.ArrayList")
            elif prop == "Date":
                imports.add("java.util.Date")
            elif prop == "Uri":
                imports.add("android.net.Uri")

        if self.do_json:
            imports.update(self.JSON_IMPORTS)
            if self.needs_jsonutil():
                imports.add("com.yelp.parcelgen.JsonUtil")
            if self.needs_jsonarray():
                imports.add("org.json.JSONArray")
        if self.make_serializable:
            imports.add("java.io.Serializable")
        imports = list(imports)
        imports.sort()

        for imp in imports:
            self.printtab("import %s;" % imp)

        self.output("")
        self.printtab("/**\n * Automatically generated Parcelable implementation for %s. DO NOT" % class_name)
        self.printtab(" * MODIFY THIS FILE MANUALLY! IT WILL BE OVERWRITTEN THE NEXT TIME")
        self.printtab(" * %s's PARCELABLE DESCRIPTION IS CHANGED." % class_name)
        self.printtab(" */")

        implements = "Parcelable"
        if self.make_serializable:
            implements += ", Serializable"
        self.printtab((self.CLASS_STR % (class_name, implements)) + "\n")

        # Protected member variables
        self.uptab()
        for typ, member in self.member_map():
            if member in transient:
                typ = "transient " + typ
            self.printtab("protected %s %s;" % (typ, self.memberize(member)))
        self.output("")

        # Parameterized Constructor
        constructor = "protected %s(" % class_name
        params = []
        for typ, member in self.member_map():
            params.append("%s %s" % (typ, member))
        constructor += "%s) {" % ", ".join(params)
        self.printtab(constructor)
        self.uptab()
        self.printtab("this();")
        for typ, member in self.member_map():
            self.printtab("%s = %s;" % (self.memberize(member), member))
        self.tablevel -= 1
        self.printtab("}\n")

        # Empty constructor for Parcelable
        self.printtab("protected %s() {" % class_name)
        self.uptab()
        self.printtab("super();")
        self.downtab()
        self.printtab("}\n")
    
        # Getters for member variables
        for typ, member in self.member_map():
            self.output(self.gen_getter(typ, member))
            self.output("")

        # Parcelable writeToParcel
        self.printtab("public int describeContents() {\n%s%sreturn 0;\n%s}" % (self.TAB_STR, self.TAB_STR, self.TAB_STR))
        self.output("")
        self.printtab("public void writeToParcel(Parcel parcel, int flags) {")
        self.uptab()
        self.output(self.gen_parcelable())
        self.downtab()
        self.printtab("}\n")

        # readFromParcel that allows subclasses to use parcelable-ness of their superclass
        self.printtab("public void readFromParcel(Parcel source) {")
        self.tablevel += 1
        i = 0
        all_members = []
        for typ in self.get_types():
            if typ == "boolean":
                self.printtab("boolean[] bools = source.createBooleanArray();")
                for j in xrange(len(props[typ])):
                    self.printtab("%s = bools[%d];" % (self.memberize(props[typ][j]), j))
            else:
                for member in props[typ]:
                    memberized = self.memberize(member)
                    list_gen = self.gen_list_unparcel(typ, memberized)
                    if list_gen:
                        self.output(list_gen)
                    elif self.array_type(typ):
                        array_gen = self.gen_array_unparcel(typ, memberized)
                        self.output(array_gen)
                    elif typ == "Date":
                        self.printtab("long date%d = source.readLong();" % i)
                        self.printtab("if (date%d != Integer.MIN_VALUE) {" % i)
                        self.uptab()
                        self.printtab("%s = new Date(date%d);" % (memberized, i))
                        self.downtab()
                        self.printtab("}")
                        i += 1
                    elif typ in self.BOX_TYPES:
                        self.printtab("%s = (%s) source.readValue(%s.class.getClassLoader());" % (memberized, typ.capitalize(), typ.capitalize()))
                    elif typ.lower() in self.NATIVE_TYPES:
                        self.printtab("%s = source.read%s();" % (memberized, typ.capitalize()))
                    elif typ in self.serializables:
                        self.printtab("%s = (%s)source.readSerializable();" % (memberized, typ))
                    else:
                        self.printtab("%s = source.readParcelable(%s.class.getClassLoader());" % (memberized, typ))
        self.tablevel -= 1
        self.printtab("}\n")
#       self.print_creator(class_name, "Parcelable.Creator")

        if self.do_json:
            self.output(self.generate_json_reader(props))
        if self.do_json_writer:
            self.output(self.generate_json_writer(props))
        self.downtab()
        self.printtab("}")

    def generate_json_reader(self, props):
        self.props = props
        fun = self.tabify("public void readFromJson(JSONObject json) throws JSONException {\n")
        self.uptab()
        # Parcelable doesn't support boolean without help, JSON does
        NATIVES = self.NATIVE_TYPES + ["boolean"]
        for typ in self.get_types():
            list_type = self.list_type(typ)
            array_type = self.array_type(typ)
            # Always protect strings with isNull check because JSONObject.optString()
            # returns the string "null" for null strings.    AWESOME.
            protect = typ not in [native for native in NATIVES if native != "String"]
            for member in props[typ]:
                # Some object members are derived and not stored in JSON
                if member in self.json_blacklist:
                    continue
                # Some members have different names in JSON
                if member in self.json_map:
                    key = self.json_map[member]
                else:
                    key = camel_to_under(member)
                # Need to check if key is defined if we have a default value too
                if member in self.default_values:
                    protect = True
                if protect:
                    fun += self.tabify("if (!json.isNull(\"%s\")) {\n" % key)
                    self.uptab()
                # we need to do some special stuff with arrays, so don't assign
                # that right away 
                if not array_type:
                    fun += self.tabify("%s = " % self.memberize(member))
                if typ.lower() == "float":
                    fun += "(float)json.optDouble(\"%s\")" % key
                elif typ.lower() in NATIVES:
                    fun += "json.opt%s(\"%s\")" % (typ.capitalize(), key)
                elif typ == "List<String>":
                    fun += "JsonUtil.getStringList(json.optJSONArray(\"%s\"))" % key
                elif typ == "Date":
                    fun += "JsonUtil.parseTimestamp(json, \"%s\")" % key
                elif typ == "Uri":
                    fun += "Uri.parse(json.getString(\"%s\"))" % key
                elif array_type:
                    classname = self.array_type(typ)
                    memberized = self.memberize(member)
                    fun += self.tabify("JSONArray jsonArray = json.getJSONArray(\"%s\");\n" % key)
                    fun += self.tabify("int arrayLen = jsonArray.length();\n")
                    fun += self.tabify("%s = new %s[arrayLen];\n" % (memberized, classname))
                    fun += self.tabify("for (int i = 0; i < arrayLen; i++) {\n")
                    self.uptab()
                    if (classname.lower() in NATIVES):
                        if (classname.lower() == "float"):
                            fun += self.tabify("%s[i] = (float) jsonArray.getDouble(i);\n" % memberized)
                        elif (classname.lower() == "byte"):
                            fun += self.tabify("%s[i] = (byte) jsonArray.getInt(i);\n" % memberized)
                        else:
                            fun += self.tabify("%s[i] = jsonArray.get%s(i);\n" % (memberized, classname.capitalize()))
                    else:
                        fun += self.tabify("%s[i] = %s.CREATOR.parse(jsonArray.getJSONObject(i));\n" % (memberized, classname.capitalize()))
                    self.downtab()
                    fun += self.tabify("}\n")
                elif list_type:
                    fun += "JsonUtil.parseJsonList(json.optJSONArray(\"%s\"), %s.CREATOR)" % (key, list_type)
                else:
                    fun += "%s.CREATOR.parse(json.getJSONObject(\"%s\"))" % (typ, key)
                if not array_type:
                    fun += ";\n"
                if protect:
                    self.downtab()
                    listmatcher = re.match(r"(?P<list_type>Array)?List(?P<content_type>[<>a-zA-Z0-9_]*)", typ)
                    if listmatcher is not None:
                        match_dict = listmatcher.groupdict()
                        fun += self.tabify("} else {\n")
                        self.uptab()
                        fun += self.tabify(("%s = " % self.memberize(member)))
                        if match_dict['list_type'] is not None and match_dict['content_type'] is not None:
                            fun += ("new %sList%s()" % (match_dict['list_type'], match_dict['content_type']))
                        else:
                            fun += "java.util.Collections.emptyList()"
                        fun += ";\n"
                        self.downtab()
                    elif member in self.default_values:
                        fun += self.tabify("} else {\n")
                        self.uptab()
                        fun += self.tabify(("%s = %s;\n" % (self.memberize(member), self.default_values[member])))
                        self.downtab()
                    fun += self.tabify("}\n")
        self.downtab()
        fun += self.tabify("}\n")
        return fun

    def generate_json_writer(self, foo):
        fun = self.tabify("public JSONObject writeJSON() throws JSONException {\n")
        self.uptab()
        fun += self.tabify("JSONObject json = new JSONObject();\n")
        # Parcelable doesn't support boolean without help, JSON does
        NATIVES = self.NATIVE_TYPES + ["boolean", "String"]
        for typ in self.get_types():
            list_type = self.list_type(typ)
            array_type = self.array_type(typ)
            # Always protect strings with isNull check because JSONObject.optString()
            # returns the string "null" for null strings.    AWESOME.
            protect = typ not in [native for native in NATIVES if native != "String"]
            for member in self.props[typ]:
                # Some object members are derived and not stored in JSON
                if member in self.json_blacklist:
                    continue
                # Some members have different names in JSON
                if member in self.json_map:
                    key = self.json_map[member]
                else:
                    key = camel_to_under(member)
                if protect:
                    fun += self.tabify("if (%s != null) {\n" % self.memberize(member))
                    self.uptab()
                if typ == "List<String>":
                    fun += self.tabify("// TODO list writing %s\n" % self.memberize(member))
                elif typ == "Date":
                    fun += self.tabify("json.put(\"%s\", %s.getTime() / 1000);\n" % (key, self.memberize(member)))
                elif typ == "Uri":
                    fun += self.tabify("json.put(\"%s\", String.valueOf(%s));\n" % (key, self.memberize(member)))
                elif list_type:
                    fun += self.tabify("// TODO LIST writing %s \n" % self.memberize(member))
                elif array_type:
                    fun += self.tabify("JSONArray array = new JSONArray();\n")
                    fun += self.tabify("for (%s temp: %s) {\n" % (array_type, self.memberize(member)))
                    self.uptab()
                    if (array_type in NATIVES):
                        # Correct for any siliness related to byte signage silliness
                        if (array_type == "byte"):
                            fun += self.tabify("array.put(temp & 0xFF);\n")
                        else:
                            fun += self.tabify("array.put(temp);\n")
                    else:
                        fun += self.tabify("array.put(temp.writeJSON());\n")
                    self.downtab()
                    fun += self.tabify("}\n")
                    fun += self.tabify("json.put(\"%s\", %s);\n" %(key, "array"))
                elif typ in NATIVES:
                    fun += self.tabify("json.put(\"%s\", %s);\n" % (key, self.memberize(member)))
                else:
                    fun += self.tabify("json.put(\"%s\", %s.writeJSON());\n" % (key, self.memberize(member)))
                if protect:
                    self.downtab()
                    fun += self.tabify("}\n")
        fun += self.tabify("return json;\n")
        self.downtab()
        fun += self.tabify("}\n")
        return fun

def camel_to_under(member):
    """ Convert NamesInCamelCase to jsonic_underscore_names"""
    s1 = re.sub('(.)([A-Z][a-z]+)', r'\1_\2', member)
    return re.sub('([a-z0-9])([A-Z])', r'\1_\2', s1).lower()

def generate_class(filePath, output):
    # Read parcelable description json
    description = json.load(open(filePath, 'r'))
    props = description.get("props") or {}
    package = description.get("package") or None
    imports = description.get("imports") or ()
    json_map = description.get("json_map") or {}
    default_values = description.get("default_values") or {}
    transient = description.get("transient") or []
    make_serializable = description.get("make_serializable")
    do_json_writer = description.get("do_json_writer")
    json_blacklist = description.get("json_blacklist") or []
    serializables = description.get("serializables") or ()
    if 'do_json' in description:
        do_json = description.get("do_json")
    else:
        do_json = False
    class_name = "_" + os.path.basename(filePath).split(".")[0]

    generator = ParcelGen()
    generator.json_map = json_map
    generator.json_blacklist = json_blacklist
    generator.serializables = serializables
    generator.do_json = do_json
    generator.do_json_writer = do_json_writer
    generator.make_serializable = make_serializable

    generator.default_values = default_values
    if output:
        if (os.path.isdir(output)): # Resolve file location based on package + path
            dirs = package.split(".")
            dirs.append(class_name + ".java")
            targetFile = os.path.join(output, *dirs)
            # Generate child subclass if it doesn't exist
            if class_name.startswith("_"):
                child = class_name[1:]
                new_dirs = package.split(".")
                new_dirs.append(child + ".java")
                child_file = os.path.join(output, *new_dirs)
                if not os.path.exists(child_file):
                    generator.outfile = open(child_file, 'w')
                    generator.print_child(child, package)
        generator.outfile = open(targetFile, 'w')
    generator.print_gen(props, class_name, package, imports, transient)


if __name__ == "__main__":
    usage = """USAGE: %s parcelfile [destination]

Generates a parcelable Java implementation for provided description file.
Writes to stdout unless destination is specified.

If destination is a directory, it is assumed to be the top level
directory of your Java source. Your class file will be written in the
appropriate folder based on its Java package.
If destination is a file, your class will be written to that file."""
    if len(sys.argv) < 2:
        print(usage % sys.argv[0])
        exit(0)
    destination = None
    if len(sys.argv) > 2:
        destination = sys.argv[2]
    source = sys.argv[1]
    # If both source and destination are directories, run in
    # fake make mode
    if (os.path.isdir(source) and os.path.isdir(destination)):
        for sourcefile in [sourcefile for sourcefile in os.listdir(source) if sourcefile.endswith(".json")]:
            print "decoding ", sourcefile
            generate_class(os.path.join(source, sourcefile), destination)
    else:
        generate_class(sys.argv[1], destination)

