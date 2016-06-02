package org.wyw.util;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class JsonUtils {

	private static final Gson gson = new Gson();

	public static String beanToJson(Object obj) {
		return gson.toJson(obj);
	}

	@SuppressWarnings("unchecked")
	public static <T> T JsonToBean(String json, Class<T> clazz) {
		return (T) gson.fromJson(json,clazz);
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T JsonToBean(String json, Type type) {
		return (T) gson.fromJson(json,type);
	}
	
	/**
	 * 转换JSON
	 * @param jsonString
	 * @return
	 */
	public static String getShowJsonString(String  jsonString){
		if(jsonString == null){
			return jsonString;
		}
		if(jsonString.contains("\\\"")){
			jsonString = jsonString.replace("\\\"", "\"");
		}
		if(jsonString.contains("\\\\")){
			jsonString = jsonString.replace("\\\\", "\\");
		}
		if(jsonString.contains("\\u003c")){
			jsonString = jsonString.replace("\\u003c", "<");
		}
		if(jsonString.contains("\\u0026")){
			jsonString = jsonString.replace("\\u0026", "&");
		}
		
		if(jsonString.contains("\\u0027")){
			jsonString = jsonString.replace("\\u0027", "'");
		}
		
		if(jsonString.contains("\\u003e")){
			jsonString = jsonString.replace("\\u003e", ">");
		}
		
		if(jsonString.contains("\\u003d")){
			jsonString = jsonString.replace("\\u003d", "=");
		}
		if(jsonString.contains("&#034;")){
			jsonString = jsonString.replace("&#034;", "\"");
		}
		return jsonString;
	}
	
	
	 /** 
     * 获取JsonObject 
     * @param json 
     * @return 
     */  
    private static JsonObject parseJson(String json){  
        JsonParser parser = new JsonParser();  
        JsonObject jsonObj = parser.parse(json).getAsJsonObject();  
        return jsonObj;  
    }
	
	public static Map<String,Object> toMap(String json){  
        return toMap(parseJson(json));  
    }
	
	/** 
     * 将JSONObjec对象转换成Map-List集合 
     * @param json 
     * @return 
     */  
    public static Map<String, Object> toMap(JsonObject json){  
        Map<String, Object> map = new HashMap<String, Object>();  
        Set<Entry<String, JsonElement>> entrySet = json.entrySet();  
        for (Iterator<Entry<String, JsonElement>> iter = entrySet.iterator(); iter.hasNext(); ){  
            Entry<String, JsonElement> entry = iter.next();  
            String key = entry.getKey();  
            Object value = entry.getValue();  
            if(value instanceof JsonArray)  
                map.put((String) key, toList((JsonArray) value));  
            else if(value instanceof JsonObject)  
                map.put((String) key, toMap((JsonObject) value));  
            else  
                map.put((String) key, value);  
        }  
        return map;  
    }
    
    /** 
     * 将JSONArray对象转换成List集合 
     * @param json 
     * @return 
     */  
    public static List<Object> toList(JsonArray json){  
        List<Object> list = new ArrayList<Object>();  
        for (int i=0; i<json.size(); i++){  
            Object value = json.get(i);  
            if(value instanceof JsonArray){  
                list.add(toList((JsonArray) value));  
            }  
            else if(value instanceof JsonObject){  
                list.add(toMap((JsonObject) value));  
            }  
            else{  
                list.add(value);  
            }  
        }  
        return list;  
    }  

}
