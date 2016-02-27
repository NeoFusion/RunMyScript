package neofusion.runmyscript.backup;

import android.content.Context;
import android.os.Environment;

import org.xmlpull.v1.XmlPullParserException;

import java.io.Closeable;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import neofusion.runmyscript.R;
import neofusion.runmyscript.model.ScriptItem;

public class BackupHelper {
    private Context mContext;
    private String mAppFolder;

    public BackupHelper(Context context) {
        mContext = context;
        String externalStoragePath = Environment.getExternalStorageDirectory().getAbsolutePath();
        mAppFolder = externalStoragePath + "/" + context.getString(R.string.app_name);
    }

    private void closeQuietly(Closeable closeable) {
        try {
            if (closeable != null) {
                closeable.close();
            }
        } catch (IOException e) {
            // Closing quietly
        }
    }

    public File[] getFiles() throws BackupException {
        File folderPath = new File(mAppFolder);
        if (!folderPath.exists() || !folderPath.isDirectory()) {
            throw new BackupException(mContext.getString(R.string.error_folder_not_found));
        }
        File[] files = folderPath.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return (pathname.isFile() && pathname.getName().endsWith(".xml"));
            }
        });
        if (files.length == 0) {
            throw new BackupException(mContext.getString(R.string.error_files_not_found));
        }

        return files;
    }

    public String exportToFile(ArrayList<ScriptItem> items) throws IOException, BackupException {
        String xmlString;
        try {
            BackupXmlCreator xmlCreator = new BackupXmlCreator();
            xmlString = xmlCreator.create(items);
        } catch (IOException e) {
            throw new BackupException(mContext.getString(R.string.error_xml_serialize));
        }
        if (xmlString.isEmpty()) {
            throw new BackupException(mContext.getString(R.string.error_backup));
        }
        File folderPath = new File(mAppFolder);
        if (!folderPath.exists() || !folderPath.isDirectory()) {
            if (!folderPath.mkdir()) {
                throw new BackupException(mContext.getString(R.string.error_creating_folder));
            }
        }
        Date date = new Date();
        String fileName = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(date);
        File exportFile = new File(folderPath.getAbsolutePath(), fileName + ".xml");
        if (exportFile.exists()) {
            throw new BackupException(String.format(
                    mContext.getString(R.string.error_file_exists),
                    exportFile.getAbsolutePath()
            ));
        }
        OutputStreamWriter outputStreamWriter = null;
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(exportFile);
            outputStreamWriter = new OutputStreamWriter(fileOutputStream, "UTF-8");
            outputStreamWriter.write(xmlString);
        } catch (Exception e) {
            throw new BackupException(mContext.getString(R.string.error_unknown));
        } finally {
            closeQuietly(outputStreamWriter);
        }

        return exportFile.getAbsolutePath();
    }

    public ArrayList<ScriptItem> importFromFile(String selectedItem) throws XmlPullParserException, IOException, BackupException {
        File importFile = new File(mAppFolder, selectedItem);
        ArrayList<ScriptItem> scriptItems = null;
        InputStreamReader inputStreamReader = null;
        try {
            FileInputStream fileInputStream = new FileInputStream(importFile);
            inputStreamReader = new InputStreamReader(fileInputStream);
            BackupXmlParser parser = new BackupXmlParser();
            scriptItems = parser.parse(inputStreamReader);
        } finally {
            closeQuietly(inputStreamReader);
        }

        return scriptItems;
    }
}