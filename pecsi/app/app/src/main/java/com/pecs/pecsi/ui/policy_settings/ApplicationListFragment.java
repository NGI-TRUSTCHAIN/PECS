package com.pecs.pecsi.ui.policy_settings;

import static com.pecs.pecsi.models.PermissionData.permissionMapping;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.pecs.pecsi.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ApplicationListFragment extends Fragment {

    private RecyclerView recyclerView;
    private ApplicationListAdapter adapter;
    private List<JSONObject> applicationList;
    private Set<String> selectedApplications;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_application_list, container, false);

        recyclerView = rootView.findViewById(R.id.recycler_view_application_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        applicationList = getThirdPartyInstalledApps();
        // TODO: When the user goes back to ApplicationList, the apps get unchecked (graphically)
        selectedApplications = new HashSet<>();
        adapter = new ApplicationListAdapter(applicationList, selectedApplications);
        recyclerView.setAdapter(adapter);

        Button btnProceed = rootView.findViewById(R.id.btn_proceed);
        btnProceed.setOnClickListener(v -> navigateToPolicySettings(v));

        return rootView;
    }

    private List<JSONObject> getThirdPartyInstalledApps() {
        List<JSONObject> applicationList = new ArrayList<>();
        PackageManager packageManager = getContext().getPackageManager();
        List<ApplicationInfo> installedApps = packageManager.getInstalledApplications(PackageManager.GET_META_DATA);
        String selfPackageName = getContext().getPackageName();

        // Filter only third-party apps
        for (ApplicationInfo appInfo : installedApps) {
            if ((appInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0 && (appInfo.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) == 0) {// && !appInfo.packageName.equals(selfPackageName)) {
                try {
//                    String[] permissions = packageManager.getPackageInfo(appInfo.packageName, PackageManager.GET_PERMISSIONS).requestedPermissions;
//                    // Initialise permissions JSON object
//                    JSONObject permissionsJson = new JSONObject();
//                    for (String flag : permissionMapping.values()) {
//                        permissionsJson.put(flag, false);
//                    }
//                    // Update permissions JSON object based on app's permissions
//                    if (permissions != null) {
//                        for (String permission : permissions) {
//                            if (permissionMapping.containsKey(permission)) {
//                                permissionsJson.put(permissionMapping.get(permission), true);
//                            }
//                        }
//                    }

                    JSONObject appJson = new JSONObject();
                    appJson.put("app", appInfo.loadLabel(packageManager).toString());
                    appJson.put("package", appInfo.packageName);
//                    appJson.put("permissions", permissionsJson);

                    applicationList.add(appJson);
                } catch (JSONException e) { // PackageManager.NameNotFoundException | JSONException e) {
                    Log.e(getTag(), "JSON error: " + appInfo.packageName, e); // Package not found or JSON error: " + appInfo.packageName, e);
                }
            }
        }
        Log.d(getTag(), "Installed third-party app list: " + applicationList);
        return applicationList;
    }

    private void navigateToPolicySettings(View view) {
        // Store selected applications in SharedPreferences
        SharedPreferences sharedPref = getActivity().getSharedPreferences("PolicySettingsPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        JSONArray jsonApplicationList = new JSONArray();
        for (JSONObject app : applicationList) {
            jsonApplicationList.put(app);
        }
        editor.putString("installedApplications", jsonApplicationList.toString());
        editor.putStringSet("selectedApplications", selectedApplications);
        if (!selectedApplications.isEmpty()){
            editor.putString("selectionType", "target");
        } else {
            editor.putString("selectionType", "global");
        }
        editor.apply();
        Log.d(getTag(), "Toggle selectedApplications changed: " + selectedApplications);

        NavController navController = Navigation.findNavController(view);
        navController.navigate(R.id.action_application_list_to_policy_settings);
    }

}