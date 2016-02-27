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