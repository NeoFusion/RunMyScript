/*
 * Copyright 2013 Evgeniy NeoFusion
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package neofusion.runmyscript.backup;

import android.util.Xml;

import org.xmlpull.v1.XmlSerializer;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;

import neofusion.runmyscript.model.ScriptItem;

public class BackupXmlCreator {
    public String create(ArrayList<ScriptItem> listItems) throws IOException {
        XmlSerializer serializer = Xml.newSerializer();
        StringWriter writer = new StringWriter();
        serializer.setOutput(writer);
        serializer.startDocument("UTF-8", true);
        serializer.startTag("", "runmyscript");
        serializer.attribute("", "version", "1");
        serializer.startTag("", "items");
        for (ScriptItem scriptItem : listItems) {
            String su = (scriptItem.getSu()) ? "true" : "false";
            String type;
            switch (scriptItem.getType()) {
                case ScriptItem.TYPE_SINGLE_COMMAND:
                    type = "cmd";
                    break;
                case ScriptItem.TYPE_PATH_TO_FILE:
                    type = "path";
                    break;
                default:
                    type = "unknown";
                    break;
            }
            serializer.startTag("", "item");
            serializer.startTag("", "name");
            serializer.text(scriptItem.getName());
            serializer.endTag("", "name");
            serializer.startTag("", "path");
            serializer.attribute("", "type", type);
            serializer.attribute("", "su", su);
            serializer.text(scriptItem.getPath());
            serializer.endTag("", "path");
            serializer.endTag("", "item");
        }
        serializer.endTag("", "items");
        serializer.endTag("", "runmyscript");
        serializer.endDocument();
        return writer.toString();
    }
}