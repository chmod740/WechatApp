package me.hupeng.app.wechat.service;

import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import com.google.gson.reflect.TypeToken;
import me.hupeng.app.wechat.chat.bean.Contact;
import me.hupeng.app.wechat.chat.bean.User;
import me.hupeng.app.wechat.conf.Configuration;
import okhttp3.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Created by admin on 2017/8/12.
 */
public class UserService {
    protected final String TAG = "###WEATHER_APP###";

    private static User currentUser;

    public static User getCurrentUser() {
        return currentUser;
    }

    public static interface LoginListener{
        public void done(User user, Exception e);
    }

    private OkHttpClient client = new OkHttpClient.Builder().build();


    public void login(String username, String password, LoginListener loginListener){
        if (username.length() == 0 ) {
            loginListener.done(null,new Exception("请输入用户名"));
            return;
        }
        if (password.length() == 0){
            loginListener.done(null,new Exception("请输入密码"));
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                String url = Configuration.BASE_HTTP_URL + "login/username/" + username + "/password/" + password;
                Request request = new Request.Builder().url(url).build();
                Call call = client.newCall(request);
                call.enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Handler handler = new Handler(Looper.getMainLooper());
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                loginListener.done(null,new Exception("网络连接失败"));
                            }
                        });
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String body = response.body().string();
                        try {

                            JsonObject jsonObject = (JsonObject) new JsonParser().parse(body);
                            int rst = jsonObject.get("rst").getAsInt();
                            if (rst == 0){
                                User user = new Gson().fromJson(jsonObject.get("data").toString(),User.class);
                                currentUser = user;
                                if (user.getNickName() == null){
                                    user.setNickName(user.getUsername());
                                }
                                if (user.getUserPhoto() != null){
                                    user.setUserPhoto(user.getUserPhoto() + "?imageView2/0/w/96/h/96/format/png/interlace/0/");
                                }
//                                new Thread(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        currentUser.setUserPhoto(createLocalImageFromUrl(user.getUserPhoto(),getLocalImgDir(),"user.jpg"));
//                                    }
//                                }).start();
                                Handler handler = new Handler(Looper.getMainLooper());
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        loginListener.done(user,null);
                                    }
                                });
                            }else {
                                Handler handler = new Handler(Looper.getMainLooper());
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        loginListener.done(null, new Exception("登录失败：" + jsonObject.get("msg").getAsString()));
                                    }
                                });
                            }
                        }catch (Exception e){
                            Handler handler = new Handler(Looper.getMainLooper());
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    loginListener.done(null,new Exception("网络连接失败"));
                                }
                            });
                        }
                    }
                });
            }
        }).start();
    }

    public static interface CheckLoginStatusListener{
        public void done(User user, Exception e);
    }

    public void checkUserLoginStatus(String ak, CheckLoginStatusListener loginStatusListener){
        new Thread(new Runnable() {
            @Override
            public void run() {
                String url = Configuration.BASE_HTTP_URL + "check_user_login_status?ak=" + ak ;
                Request request = new Request.Builder().url(url).build();
                Call call = client.newCall(request);

                Handler handler = new Handler(Looper.getMainLooper());
                call.enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                loginStatusListener.done(null,new Exception("网络连接失败"));
                            }
                        });
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String body = response.body().string();
                        JsonObject jsonObject = (JsonObject) new JsonParser().parse(body);
                        int rst  = jsonObject.get("rst").getAsInt();
                        if (rst == 0){
                            User user = new Gson().fromJson(jsonObject.get("data").toString(), User.class);
                            currentUser = user;
                            if (user.getNickName() == null){
                                user.setNickName(user.getUsername());
                            }
                            if (user.getUserPhoto() != null){
                                user.setUserPhoto(user.getUserPhoto() + "?imageView2/0/w/96/h/96/format/png/interlace/0/");
                            }

//                            new Thread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    currentUser.setUserPhoto(createLocalImageFromUrl(user.getUserPhoto(),getLocalImgDir(),"user.jpg"));
//                                }
//                            }).start();
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    loginStatusListener.done(user,null);
                                }
                            });
                        }else {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    loginStatusListener.done(null,new Exception(jsonObject.get("msg").getAsString()));
                                }
                            });
                        }
                    }
                });
            }
        }).start();
    }


    public interface GetRelationshipCountListener{
        public void done(int count, Exception e);
    }

    /**
     * 得到联系人的数目
     * */
    public void getUserRelationshipCount(String ak, GetRelationshipCountListener listener){
        new Thread(new Runnable() {
            @Override
            public void run() {
                String url = Configuration.BASE_HTTP_URL +  "get_relationship_list_count?ak=" + ak;
                Request request = new Request.Builder().url(url).build();
                Call call = client.newCall(request);
                call.enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                listener.done(0,new Exception("网络连接失败"));
                            }
                        });
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        JsonObject jsonObject = (JsonObject) new JsonParser().parse(response.body().string());
                        int rst = jsonObject.get("rst").getAsInt();
                        if (rst == 0){
                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {
                                    int count = jsonObject.get("data").getAsInt();
                                    listener.done(count,null);
                                }
                            });
                        }else {
                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {
                                    listener.done(0,new Exception("联系人信息获取失败"));
                                }
                            });
                        }

                    }
                });

            }
        }).start();
    }

    public interface GetRelationshipListener{
        public void done(List<Contact> contacts, Exception e);
    }


    /**
     * 得到联系人的详细信息
     * */
    public void getUserRelationship(String ak, GetRelationshipListener listener){
        new Thread(new Runnable() {
            @Override
            public void run() {
                String url = Configuration.BASE_HTTP_URL +  "get_relationship_list?ak=" + ak;
                Request request = new Request.Builder().url(url).build();
                Call call = client.newCall(request);
                call.enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                listener.done(null,new Exception("网络连接失败"));
                            }
                        });
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        JsonObject jsonObject = (JsonObject) new JsonParser().parse(response.body().string());
                        int rst = jsonObject.get("rst").getAsInt();
                        if (rst == 0){

                            List<Contact>contacts = new Gson().fromJson(jsonObject.get("data").toString(),new TypeToken<List<Contact>>(){}.getType());

                            /**
                             * 设置用户头像的目录
                             * */

                            for (Contact contact : contacts){
                                contact.setUserId(contact.getId());
                                contact.setId(0);

                                if (contact.getNickName() == null){
                                    contact.setNickName(contact.getUsername());
                                }

                                if (contact.getUserPhoto() != null){
                                    //设置头像的分辨率

                                    String userPhotoUrl = contact.getUserPhoto() + "?imageView2/0/w/96/h/96/format/png/interlace/0/";
                                    contact.setUserPhoto(userPhotoUrl);
//                                    String fileName = "user_" + contact.getUserId() + ".jpg";
//                                    String localFilePath = createLocalImageFromUrl(contact.getUserPhoto(),getLocalImgDir(),fileName);
//
//                                    contact.setUserPhoto("file://" + localFilePath);

                                }

                            }

                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {

                                    listener.done(contacts,null);
                                }
                            });
                        }else {
                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {
                                    listener.done(null,new Exception("联系人信息获取失败"));
                                }
                            });
                        }

                    }
                });

            }
        }).start();
    }


    private String createLocalImageFromUrl(String url,File imgDir,String fileName){

        Request imageRequest = new Request.Builder().url(url).build();
        Call imageCall = client.newCall(imageRequest);
        try {
            Response imgResponse = imageCall.execute();
            InputStream is = null;
            byte[] buf = new byte[2048];
            int len = 0;
            FileOutputStream fos = null;
//                                                String dirPathStr = Environment.getExternalStorageDirectory().getAbsolutePath() + "/WeatherApp/";

            try {
                is = imgResponse.body().byteStream();
                long total = imgResponse.body().contentLength();
                File file = new File(imgDir, fileName);
                if (file.exists()){
                    file.delete();
                }
                fos = new FileOutputStream(file);
                long sum = 0;
                while ((len = is.read(buf)) != -1) {
                    fos.write(buf, 0, len);
                    sum += len;
                }
                fos.flush();
                Log.d(TAG, "图片下载成功,路径:" + file.getAbsolutePath());
//                contact.setUserPhoto("file://" + file.getAbsolutePath());
                return file.getAbsolutePath();
            } catch (Exception e) {
                Log.d(TAG, "下载图片时发生错误");
            } finally {
                try {
                    if (is != null)
                        is.close();
                } catch (IOException e) {
                }
                try {
                    if (fos != null)
                        fos.close();
                } catch (IOException e) {
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
            Log.i(TAG,"下载图片时发生错误");
        }
        return "";
    }

    /**
     * 设置图片的本地存储路径
     * */
    private File getLocalImgDir(){
        File weatherImgDir = new File(Environment.getExternalStorageDirectory(),  "weather");
        if (!weatherImgDir.exists()){
            weatherImgDir.mkdirs();
        }
        return weatherImgDir;
    }

}

