package neofusion.runmyscript;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import neofusion.runmyscript.fragment.ExportFragment;
import neofusion.runmyscript.fragment.ImportFragment;
import neofusion.runmyscript.fragment.ProcessFragment;
import neofusion.runmyscript.fragment.ScriptItemListFragment;
import neofusion.runmyscript.utils.PermissionUtil;

public class MainActivity extends AppCompatActivity implements ScriptItemListFragment.OnFragmentInteractionListener, FragmentManager.OnBackStackChangedListener, ActivityCompat.OnRequestPermissionsResultCallback {
    private View mLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mLayout = findViewById(R.id.main_layout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportFragmentManager().addOnBackStackChangedListener(this);
        if (savedInstanceState == null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            ScriptItemListFragment listFragment = new ScriptItemListFragment();
            transaction.add(R.id.fragmentContainer, listFragment);
            transaction.commit();
        } else {
            onBackStackChanged();
        }
    }

    public void runProcess(String[] cmd) {
        ProcessFragment processFragment = ProcessFragment.newInstance(cmd);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragmentContainer, processFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public void runImport(String file) {
        ImportFragment importFragment = ImportFragment.newInstance(file);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragmentContainer, importFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void runExport() {
        ExportFragment exportFragment = ExportFragment.newInstance();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragmentContainer, exportFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onBackStackChanged() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            boolean enableBack = getSupportFragmentManager().getBackStackEntryCount() > 0;
            actionBar.setDisplayHomeAsUpEnabled(enableBack);
        }
    }

    private boolean isBackEnabled() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentById(R.id.fragmentContainer);
        if (fragment != null) {
            if (fragment instanceof ProcessFragment) {
                ProcessFragment processFragment = (ProcessFragment) fragment;
                if (processFragment.hasRunningLoaders()) {
                    return false;
                }
            } else if (fragment instanceof ImportFragment) {
                ImportFragment importFragment = (ImportFragment) fragment;
                if (importFragment.hasRunningLoaders()) {
                    return false;
                }
            } else if (fragment instanceof ExportFragment) {
                ExportFragment exportFragment = (ExportFragment) fragment;
                if (exportFragment.hasRunningLoaders()) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        if (isBackEnabled()) {
            getSupportFragmentManager().popBackStack();
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        if (isBackEnabled()) {
            super.onBackPressed();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case ScriptItemListFragment.REQUEST_STORAGE_FOR_IMPORT:
                if (PermissionUtil.verifyPermissions(grantResults)) {
                    Snackbar.make(mLayout, R.string.permission_available_storage_import, Snackbar.LENGTH_SHORT).show();
                } else {
                    Snackbar.make(mLayout, R.string.permissions_not_granted, Snackbar.LENGTH_SHORT).show();
                }
                break;
            case ScriptItemListFragment.REQUEST_STORAGE_FOR_EXPORT:
                if (PermissionUtil.verifyPermissions(grantResults)) {
                    Snackbar.make(mLayout, R.string.permission_available_storage_export, Snackbar.LENGTH_SHORT).show();
                } else {
                    Snackbar.make(mLayout, R.string.permissions_not_granted, Snackbar.LENGTH_SHORT).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                break;
        }
    }
}