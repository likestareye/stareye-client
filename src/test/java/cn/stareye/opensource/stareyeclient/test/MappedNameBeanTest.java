package cn.stareye.opensource.stareyeclient.test;

import cn.stareye.opensource.stareyeclient.Response;
import cn.stareye.opensource.stareyeclient.StandardStarEyeClient;
import cn.stareye.opensource.stareyeclient.StarEyeClient;
import cn.stareye.opensource.stareyeclient.StarEyeClientConf;
import cn.stareye.opensource.stareyeclient.utils.JsonUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.Map;

/**
 * @author: wjf
 * @date: 2022/8/8
 */
public class MappedNameBeanTest {

    public static StarEyeClient starEyeClient;

    @Before
    public void before() {
        StarEyeClientConf conf = new StarEyeClientConf();
        conf.setServerAddress("http://localhost:8400/");
        conf.setContentPath("/enums");
        starEyeClient = new StandardStarEyeClient(conf);
    }


    @Test
    public void entity() {
        User user = new User();
        user.setName("wjf");
        user.setAge(18);

        User user1 = new User();
        user1.setName("wjf2");
        user1.setAge(20);
        user.setChild(user1);
        Response execute = starEyeClient.execute(user);
    }

    @Test
    public void post() {
        PostReq postReq = new PostReq();
        postReq.setName("wjf");
        postReq.setAge(18);
        PostData postData = new PostData();
        postData.setData1("data1");
        postData.setData2("data2");
        postReq.setPostData(postData);
        //postReq.setFile(new File("D:\\data\\微信图片_20220818162530.jpg"));
        PostRes res = starEyeClient.execute(postReq);
        System.out.println(res);
    }

    @Test
    public void json() {
        String json =
                """
                    {
                        "success": {
                            "message": "Welcome to JSON Viewer Pro",
                            "status_code": 200
                        },
                        "wjf": {
                            "age": 18,
                            "sex": "male"
                        }
                    }
                """;
        Map<String, Object> objectMap = JsonUtils.fromJson(json, new TypeReference<Map<String, Object>>() {
        });
        System.out.println(objectMap);
        ObjectNode jsonNodes = JsonUtils.readObjectTree(json);
        System.out.println(jsonNodes);
    }

}
