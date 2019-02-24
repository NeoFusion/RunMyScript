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

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import neofusion.runmyscript.model.ScriptItem;

public class BackupXmlParser {
    public ArrayList<ScriptItem> parse(InputStreamReader inputStreamReader) throws IOException, XmlPullParserException, BackupException {
        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        XmlPullParser parser = factory.newPullParser();
        parser.setInput(inputStreamReader);
        parser.nextTag();
        return readRunmyscriptTag(parser);
    }

    private ArrayList<ScriptItem> readRunmyscriptTag(XmlPullParser parser) throws IOException, XmlPullParserException, BackupException {
        parser.require(XmlPullParser.START_TAG, null, "runmyscript");
        String version = parser.getAttributeValue(null, "version");
        if (version.equals("1")) {
            parser.nextTag();
            return readItemsTag(parser);
        } else {
            throw new BackupException("Unknown version");
        }
    }

    private ArrayList<ScriptItem> readItemsTag(XmlPullParser parser) throws IOException, XmlPullParserException {
        ArrayList<ScriptItem> scriptItems = new ArrayList<>();
        parser.require(XmlPullParser.START_TAG, null, "items");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String tagName = parser.getName();
            if (tagName.equals("item")) {
                scriptItems.add(readItemTag(parser));
            } else {
                skip(parser);
            }
        }
        return scriptItems;
    }

    private ScriptItem readItemTag(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, null, "item");
        String name = null;
        String path = null;
        int type = 0;
        boolean su = false;
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String tagName = parser.getName();
            if (tagName.equals("name")) {
                name = readNameTag(parser);
            } else if (tagName.equals("path")) {
                ScriptItem data = readPathTag(parser);
                path = data.getPath();
                type = data.getType();
                su = data.getSu();
            } else {
                skip(parser);
            }
        }
        return new ScriptItem(name, path, type, su);
    }

    private String readNameTag(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, null, "name");
        String name = readText(parser);
        parser.require(XmlPullParser.END_TAG, null, "name");
        return name;
    }

    private ScriptItem readPathTag(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, null, "path");
        String typeString = parser.getAttributeValue(null, "type");
        String su = parser.getAttributeValue(null, "su");
        String path = readText(parser);
        parser.require(XmlPullParser.END_TAG, null, "path");
        int typeInt;
        if (typeString.equals("cmd")) {
            typeInt = ScriptItem.TYPE_SINGLE_COMMAND;
        } else if (typeString.equals("path")) {
            typeInt = ScriptItem.TYPE_PATH_TO_FILE;
        } else {
            typeInt = 0;
        }
        return new ScriptItem(null, path, typeInt, su.equals("true"));
    }

    private String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        return result;
    }

    private void skip(XmlPullParser parser) throws IOException, XmlPullParserException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }
}