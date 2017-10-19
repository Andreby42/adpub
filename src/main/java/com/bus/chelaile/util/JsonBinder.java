package com.bus.chelaile.util;



import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;
import org.codehaus.jackson.type.TypeReference;


/**
 * Jackson的简单封装.
 * 
 * @author calvin
 */
public class JsonBinder {
	
	public final static String nonNull = "NON_NULL";
	
	public final static String always = "ALWAYS";
	
	//public final static String non_default = "NON_DEFAULT";

	//创建只输出非空属性到Json字符串的Binder.
	private static ObjectMapper mapper_NonNull;
	//创建输出全部属性到Json字符串的Binder.
	private static ObjectMapper mapper_ALWAYS;
	//创建只输出初始值被改变的属性到Json字符串的Binder.
	private static ObjectMapper mapper_NON_DEFAULT;
	
	static{
		mapper_NonNull = new ObjectMapper();
		//设置输出包含的属性
		mapper_NonNull.getSerializationConfig().setSerializationInclusion(Inclusion.NON_NULL);
		//设置输入时忽略JSON字符串中存在而Java对象实际没有的属性
		mapper_NonNull.getDeserializationConfig().set(
				org.codehaus.jackson.map.DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		
		mapper_ALWAYS = new ObjectMapper();
		//设置输出包含的属性
		mapper_ALWAYS.getSerializationConfig().setSerializationInclusion(Inclusion.ALWAYS);
		//设置输入时忽略JSON字符串中存在而Java对象实际没有的属性
		mapper_ALWAYS.getDeserializationConfig().set(
				org.codehaus.jackson.map.DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		
		
		mapper_NON_DEFAULT = new ObjectMapper();
		//设置输出包含的属性
		mapper_NON_DEFAULT.getSerializationConfig().setSerializationInclusion(Inclusion.NON_DEFAULT);
		//设置输入时忽略JSON字符串中存在而Java对象实际没有的属性
		mapper_NON_DEFAULT.getDeserializationConfig().set(
				org.codehaus.jackson.map.DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	}

	/**
	 * 如果JSON字符串为Null或"null"字符串,返回Null.
	 * 如果JSON字符串为"[]",返回空集合.
	 * 
	 * 如需读取集合如List/Map,且不是List<String>这种简单类型时使用如下语句:
	 * List<MyBean> beanList = binder.getMapper().readValue(listString, new TypeReference<List<MyBean>>() {});

	 */
	public static <T> T fromJson(String jsonString, Class<T> clazz,String type) throws Exception {
		if( type.equals("ALWAYS") ){
			return mapper_ALWAYS.readValue(jsonString, clazz);
		}else	if( type.equals("NON_NULL") ){
			return mapper_NonNull.readValue(jsonString, clazz);
		}else	if( type.equals("NON_DEFAULT") ){
			return mapper_NON_DEFAULT.readValue(jsonString, clazz);
		}
		throw new IllegalArgumentException("没有匹配的type类型");
		
	}
	
	public static <T> T fromJsonList(String listString, TypeReference<T> typeRef,String type) throws Exception {
		if( type.equals("ALWAYS") ){
			return mapper_ALWAYS.readValue(listString, typeRef);
		}else	if( type.equals("NON_NULL") ){
			return mapper_NonNull.readValue(listString, typeRef);
		}else	if( type.equals("NON_DEFAULT") ){
			return mapper_NON_DEFAULT.readValue(listString, typeRef);
		}
		throw new IllegalArgumentException("没有匹配的type类型");
	}
	
	/**
	 * 如果对象为Null,返回"null".
	 * 如果集合为空集合,返回"[]".
	 */
	public static String toJson(Object object,String type) throws Exception {
		if( type.equals("ALWAYS") ){
			return mapper_ALWAYS.writeValueAsString(object);
		}else	if( type.equals("NON_NULL") ){
			return mapper_NonNull.writeValueAsString(object);
		}else	if( type.equals("NON_DEFAULT") ){
			return mapper_NON_DEFAULT.writeValueAsString(object);
		}
		throw new IllegalArgumentException("没有匹配的type类型");
	
	}


}
