package cn.sunnysky.user;

import cn.sunnysky.IntegratedManager;
import cn.sunnysky.security.SecurityManager;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class UserManager {
    private ArrayList<User> allUsers = new ArrayList<>();
    private static final String INDEX = "user_index";

    public UserManager() {
        initialize();
    }

    private void initialize(){
        IntegratedManager.fileManager.createNewFileInstance(INDEX);
        Map<String,String> map = IntegratedManager.fileManager.readSerializedDataFromFile(INDEX);

        assert map != null;
        map.values().forEach(this::loadUser);
    }

    private void loadUser(String targetCfgFile){
        Map<String,String> map = IntegratedManager.fileManager.readSerializedDataFromFile(targetCfgFile);

        assert map != null;
        User user = new User(map.get("UUID"),UserPermission.valueOf(map.get("AUTH")),map.get("USN"));
        user.changePwd(map.get("PWD"));
        allUsers.add(user);
    }

    @Nullable
    public User findUserByName(String userName){
        for (User u:
             allUsers) {
            if(u.userName.contentEquals(userName)) return u;
        }
        return null;
    }

    public void createNewUser(String userName,UserPermission permission,String encryptedPwd){
        if(findUserByName(userName) != null){
            IntegratedManager.logger.log("Username already exists!");
            return;
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
        allUsers.add(user);
    }

    public String login(String userName, String encryptedPwd){
        User u = findUserByName(userName);
        if(u.encryptedPwd.contentEquals(encryptedPwd)){
            String code =SecurityManager.hashNTLM(u.UUID + "-" +
                    IntegratedManager.logger.getFormattedTime());
            allUsers.remove(u);
            allUsers.add(u.activate(code));
            return code;
        }
        return "Incorrect password!";
    }

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

    public class ActiveUser extends User{

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
