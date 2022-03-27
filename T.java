import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;


public class T {
    public static void main(String[] args) {
        new T().connection();
    }

    //发起请求
    private void connection(){
        String rs = "";
        CloseableHttpClient httpClient = HttpClients.createDefault();
        //学校，学号，经度，维度，省，市，行政区，街道
        Self self = new Self("", "", 0,
                0, "江西省", "XX市", "XX区/县", "街道");
        String position = self.province + self.city + self.district + self.street;
        JSONObject js = addJson(self, position);
        String url = "https://fxgl.jx.edu.cn/" + self.school + "/studentQd/studentIsQd";        //是否已签到判断
        HttpGet httpGet = new HttpGet("https://fxgl.jx.edu.cn/" + self.school
                + "/public/homeQd?loginName=" + self.sno + "&loginType=0");                     //必要！勿删！
        HttpPost httpPost = new HttpPost(url);
        HttpPost qd = new HttpPost("https://fxgl.jx.edu.cn/" + self.school + "/studentQd/saveStu"); //打卡请求
        StringEntity s = new StringEntity(js.toJSONString(), "utf-8");
        qd.setEntity(s);
        try {
            CloseableHttpResponse execute = httpClient.execute(httpGet);
            httpPost.addHeader("Host", "fxgl.jx.edu.cn");
            httpPost.addHeader("Origin", "https://fxgl.jx.edu.cn");
            httpPost.addHeader("Connection", "keep-alive");
            httpPost.addHeader("Accept", "*/*");
            httpPost.addHeader("X-Requested-With", "XMLHttpRequest");
            httpPost.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/90.0.4430.212 Safari/537.36");
            httpPost.addHeader("Content-type", "application/x-www-form-urlencoded");
            httpPost.addHeader("Referer", "https://fxgl.jx.edu.cn/" + self.school + "/user/qdbp");
            httpPost.addHeader("Accept-Encoding", "gzip, deflate");
            httpPost.addHeader("Accept-Language", "zh-CN,zh;q=0.9,en-US;q=0.8,en;q=0.7");
            CloseableHttpResponse response = httpClient.execute(httpPost);
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                //按指定编码转换结果实体为String类型
                JSONObject jsonObject = JSONObject.parseObject(EntityUtils.toString(entity, "UTF-8"));
                if (jsonObject.get("data").equals(1)){
                    System.out.println(self.sno + "已签到");
                }else{
                    System.out.println(self.sno + "未签到");
                    System.out.println("开始签到————————————————");
                    CloseableHttpResponse qdd = httpClient.execute(qd);
                    HttpEntity qde = qdd.getEntity();
                    if(qde != null){
                        jsonObject = JSONObject.parseObject(EntityUtils.toString(qde, "UTF-8"));
                        System.out.println("结果：" + jsonObject);
                    }
                }
            }
            response.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    //数据
    private JSONObject addJson(Self self, String position){
        JSONObject json = new JSONObject();
        json.put("xszt", "0");
        json.put("lng", self.jing);
        json.put("lat", self.wei);
        json.put("province", position);
        json.put("city", self.city);
        json.put("district", self.district);
        json.put("street", self.street);
        json.put("sddlwz", position);
        json.put("mqtw", "0");
        json.put("mqtwxq", "");
        json.put("zddlwz", position);
        json.put("bprovince", self.province);
        json.put("bcity", self.city);
        json.put("bdistrict", self.district);
        json.put("bstreet", self.street);
        json.put("sprovince", self.province);
        json.put("scity", self.city);
        json.put("sdistrict", self.district);
        json.put("sfby", "1");
        json.put("jkzk", "0");
        json.put("jkzkxq", "");
        json.put("sfgl", "1");
        json.put("gldd", "");
        return json;
    }

    class Self{
        private String school;
        private String sno;
        private double jing;
        private double wei;
        private String province;
        private String city;
        private String district;
        private String street;

        public Self(String school, String sno, double jing, double wei, String province, String city, String district, String street) {
            this.school = school;
            this.sno = sno;
            this.jing = jing;
            this.wei = wei;
            this.province = province;
            this.city = city;
            this.district = district;
            this.street = street;
        }
    }

}
