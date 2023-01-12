package com.example.vehicleinsurance;
import android.content.Context;
import android.content.SharedPreferences;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SharedPreferencesHandler {
    public static void updateClaim(Context context, Claim claim, String userId){
        SharedPreferences pref = context.getSharedPreferences("Claims" + userId, 0);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(claim.id + "status", claim.status);
        editor.putString(claim.id + "description", claim.description);
        editor.putString(claim.id + "location", claim.location);
        editor.putString(claim.id + "photo", claim.photo);
        editor.apply();
    }

    public static Claim getClaim(Context context, String userId, String claimId) {
        SharedPreferences pref = context.getSharedPreferences("Claims" + userId, 0);
        if (!pref.contains(claimId + "status"))
            return null;
        Claim claim = new Claim();
        claim.id = claimId;
        claim.status = pref.getString(claimId + "status", "na");
        claim.description = pref.getString(claimId + "description", "na");
        claim.location = pref.getString(claimId + "location", "na");
        claim.photo = pref.getString(claimId + "photo", "na");

        return claim;
    }

    public static Claim[] getClaims(Context context, String userId) {
        SharedPreferences pref = context.getSharedPreferences("Claims" + userId, 0);
        int maxNumberOfClaims = 5;
        int i = 0;
        List<Claim> claims = new ArrayList<>();
        claims.add(new Claim());
        while (i < maxNumberOfClaims) {
            String claimId = String.valueOf(i);
            if (!pref.contains(claimId + "status"))
                return claims.toArray(new Claim[0]);

            Claim claim = new Claim();
            claim.id = claimId;
            claim.status = pref.getString(claimId + "status", "na");
            claim.description = pref.getString(claimId + "description", "na");
            claim.location = pref.getString(claimId + "location", "na");
            claim.photo = pref.getString(claimId + "photo", "na");
            claims.add(claim);
            i += 1;
        }

        return claims.toArray(new Claim[0]);
    }

    public static void setNeedServerUpdate(Context context, String userId, String missing) {
        SharedPreferences pref = context.getSharedPreferences("Claims" + userId, 0);
        Set<String> needServerUpdate = pref.getStringSet("NeedServerUpdate", new HashSet<>());
        Set<String> needServerAdd = pref.getStringSet("NeedServerAdd", new HashSet<>());
        if (needServerAdd.contains(missing))
            return;
        SharedPreferences.Editor editor = pref.edit();
        needServerUpdate.add(missing);
        editor.putStringSet("NeedServerUpdate", needServerUpdate);
        editor.apply();
    }

    public static void setNeedServerAdd(Context context, String userId, String missing) {
        SharedPreferences pref = context.getSharedPreferences("Claims" + userId, 0);
        Set<String> needServerAdd = pref.getStringSet("NeedServerAdd", new HashSet<>());
        needServerAdd.add(missing);
        SharedPreferences.Editor editor = pref.edit();
        editor.putStringSet("NeedServerAdd", needServerAdd);
        editor.apply();
    }

    public static String[] getNeedServerAdd(Context context, String userId) {
        SharedPreferences pref = context.getSharedPreferences("Claims" + userId, 0);
        Set<String> needServerAdd = pref.getStringSet("NeedServerAdd", new HashSet<>());
        return needServerAdd.toArray(new String[0]);
    }

    public static void setNeedUploadImage(Context context, String userId, String fileName) {
        SharedPreferences pref = context.getSharedPreferences("Claims" + userId, 0);
        Set<String> needFileUpload = pref.getStringSet("NeedImageUpload", new HashSet<>());
        SharedPreferences.Editor editor = pref.edit();
        needFileUpload.add(fileName);
        editor.putStringSet("NeedServerUpdate", needFileUpload);
        editor.apply();
    }

    public static String[] getNeedUploadImage(Context context, String userId) {
        SharedPreferences pref = context.getSharedPreferences("Claims" + userId, 0);
        Set<String> needServerAdd = pref.getStringSet("NeedImageUpload", new HashSet<>());
        return needServerAdd.toArray(new String[0]);
    }

    public static void removeNeedServerUpdate(Context context, String userId) {
        SharedPreferences pref = context.getSharedPreferences("Claims" + userId, 0);
        SharedPreferences.Editor editor = pref.edit();
        editor.remove("NeedServerUpdate");
        editor.remove("NeedServerAdd");
        editor.remove("NeedImageUpload");
        editor.apply();
    }

    public static String[] getNeedServerUpdate(Context context, String userId) {
        SharedPreferences pref = context.getSharedPreferences("Claims" + userId, 0);
        Set<String> needServerUpdate = pref.getStringSet("NeedServerUpdate", new HashSet<>());
        return needServerUpdate.toArray(new String[0]);
    }

    public static void removeClaimData(Context context, String userId) {
        SharedPreferences pref = context.getSharedPreferences("Claims" + userId, 0);
        SharedPreferences.Editor editor = pref.edit();
        editor.clear();
        editor.apply();
    }

    public static void writeLoginInfo(Context context, String userId, String email, String md5pass) {
        SharedPreferences preferences = context.getSharedPreferences("login", 0);
        SharedPreferences.Editor editor = preferences.edit();
        Set<String> passUserIds = preferences.getStringSet(md5pass, new HashSet<>());
        passUserIds.add(userId);
        editor.putString(email, userId);
        editor.putStringSet(md5pass, passUserIds);
        editor.apply();
    }

    public static void replaceLoginInfo(Context context, String userId, String oldMd5Pass, String md5pass) {
        SharedPreferences preferences = context.getSharedPreferences("login", 0);
        SharedPreferences.Editor editor = preferences.edit();
        Set<String> passUserIds = preferences.getStringSet(md5pass, new HashSet<>());
        Set<String> oldPassUserIds = preferences.getStringSet(oldMd5Pass, new HashSet<>());

        if (oldPassUserIds.contains(userId))
            oldPassUserIds.remove(userId);
        if (oldPassUserIds.size() == 0)
            editor.remove(oldMd5Pass);
        else
            editor.putStringSet(oldMd5Pass, oldPassUserIds);

        passUserIds.add(userId);
        editor.putStringSet(md5pass, passUserIds);
        editor.apply();
    }

    public static String localLogin(Context context, String email, String md5pass) {
        SharedPreferences preferences = context.getSharedPreferences("login",0);
        String emailUserId = preferences.getString(email,"");
        Set<String> passUserIds = preferences.getStringSet(md5pass, new HashSet<>());

        if(passUserIds.contains(emailUserId))
            return emailUserId;

        return "";
    }

    public static void clearLoginInfo(Context context, String email, String md5Pass) {
        SharedPreferences preferences = context.getSharedPreferences("login", 0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove(email);
        editor.remove(md5Pass);
        editor.apply();
    }

    public static void setNeedPasswordChange(Context context, String email) {
        SharedPreferences pref = context.getSharedPreferences("passwordChange", 0);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(email, true);
        editor.apply();
    }

    public static boolean getNeedPasswordChange(Context context, String email) {
        SharedPreferences pref = context.getSharedPreferences("passwordChange", 0);
        return pref.getBoolean(email, false);
    }

    public static void removeNeedPasswordChange(Context context, String email) {
        SharedPreferences pref = context.getSharedPreferences("passwordChange", 0);
        SharedPreferences.Editor editor = pref.edit();
        editor.remove(email);
        editor.apply();
    }
}
