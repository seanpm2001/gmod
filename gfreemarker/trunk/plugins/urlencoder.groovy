import com.lingway.groovy.text.freemarker.IGroovyFreeMarkerPlugin

class urlencoder implements IGroovyFreeMarkerPlugin {
	String transform(Map params, String content) {
		URLEncoder.encode(content);
	}
}