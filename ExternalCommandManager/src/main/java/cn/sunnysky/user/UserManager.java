package cn.sunnysky.user;

import cn.sunnysky.IntegratedManager;
import cn.sunnysky.api.annotation.Side;
import cn.sunnysky.api.annotation.SideOnly;
import cn.sunnysky.security.SecurityManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static cn.sunnysky.user.UserPermission.UNKNOWN;

public class UserManager {
    private ArrayList<User> registeredUsers = new ArrayList<>();
    private Map<String,ActiveUser> activeUsers = new HashMap<>();
    private static final String INDEX = "user_index";
    private static User defaultUser;

    public UserManager() {
        initialize();
        defaultUser = new User("default",UNKNOWN,"default");
    }

    @NotNull
    @SideOnly(value = Side.CLIENT)
    public User getDefaultUser(){
        return defaultUser;
    }

    public ArrayList<User> getRegisteredUsers() {
        return registeredUsers;
    }

    @SuppressWarnings("NewApi")
    private void initialize(){
        Map<String,String> map = IntegratedManager.fileManager.readSerializedDataFromFile(INDEX);

        if (map != null)
            map.values().forEach(this::loadUser);
    }

    private void loadUser(String targetCfgFile){
        Map<String,String> map = IntegratedManager.fileManager.readSerializedDataFromFile(targetCfgFile);

        assert map != null;
        User user = new User(map.get("UUID"),UserPermission.valueOf(map.get("AUTH")),map.get("USN"));
        user.changePwd(map.get("PWD"));
        registeredUsers.add(user);
    }

    @Nullable
    public User findUserByName(String userName){
        for (User u:
                registeredUsers) {
            if(u.userName.contentEquals(userName)) return u;
        }
        return null;
    }

    public boolean createNewUser(String userName,UserPermission permission,String encryptedPwd){
        if(findUserByName(userName) != null){
            IntegratedManager.logger.log("Username already exists!");
            return false;
        }
        String timeStamp = IntegratedManager.logger.getFormattedTime();
        String UUID = SecurityManager.hashNTLM(userName + "-" + timeStamp);

        Map<String,String> userData = new HashMap<>();
        userData.put("UUID", UUID);
        userData.put("AUTH", String.valueOf(permission));
        userData.put("USN", userName);
        userData.put("PWD", encryptedPwd);
        IntegratedManager.fileManager.writeSerializedData(userData,UUID);

        Map<String,String> indexData = new HashMap<>();
        indexData.put(userName, UUID);
        IntegratedManager.fileManager.writeSerializedData(indexData,INDEX);

        User user = new User(UUID,permission,userName);
        user.changePwd(encryptedPwd);
        registeredUsers.add(user);

        return true;
    }

    public String login(String userName, String encryptedPwd){
        User u = findUserByName(userName);

        if(u == null) return "ERR: Not registered!";
        if(u.encryptedPwd.contentEquals(encryptedPwd)){
            String code =SecurityManager.hashNTLM(u.UUID + "-" +
                    IntegratedManager.logger.getFormattedTime());

            final ActiveUser activeUser = u.activate(code);
            activeUsers.put(code,activeUser);

            return code;
        }
        return "ERR: Incorrect password!";
    }

    public void logout(String temporaryUserActivationCode){
        ActiveUser activeUser = activeUsers.get(temporaryUserActivationCode);
        if (activeUser != null){
            activeUsers.remove(temporaryUserActivationCode);
            activeUser.deactivate();
        }
    }

    public User getUserByTUAC(String temporaryUserActivationCode){ return activeUsers.get(temporaryUserActivationCode); }

    public UserPermission getUserPermission(String temporaryUserActivationCode){ return activeUsers.get(temporaryUserActivationCode).permission;}

    public static void main(String[] args) {
        UserManager manager = new UserManager();
        manager.createNewUser("Vivi",UserPermission.STUDENT,
                SecurityManager.hashNTLM("Darjeeling"));
        IntegratedManager.logger.log(manager.login("Vivi",SecurityManager.hashNTLM("Darjeeling")));
    }

    public class User {
        public String userName;
        protected final String UUID;
        protected UserPermission permission;
        protected boolean activeStatus = false;
        private String encryptedPwd = SecurityManager.hashNTLM("123456");

        public User(String UUID, UserPermission permission,String userName){
            this.UUID = UUID;
            this.permission = permission;
            this.userName = userName;
        }

        public User(User user) {
            this.UUID = user.UUID;
            this.permission = user.permission;
            this.userName = user.userName;
        }

        public void changePwd(String encryptedPwd){ this.encryptedPwd = encryptedPwd; }

        public ActiveUser activate(String code){
            return new ActiveUser(this,code);
        }
    }


    public final class ActiveUser extends User{

        private String temporaryUserActivationCode = null;

        public ActiveUser(User user,String code) {
            super(user.UUID, user.permission,user.userName);
            this.temporaryUserActivationCode = code;
            this.activeStatus = true;
        }

        public String getTemporaryUserActivationCode() {
            return temporaryUserActivationCode;
        }

        public User deactivate(){
            this.temporaryUserActivationCode = null;
            this.activeStatus = false;
            return new User(this);
        }

    }
}
