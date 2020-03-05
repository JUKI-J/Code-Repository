import java.io.IOException;
import java.util.regex.Pattern;

import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import com.fasterxml.jackson.core.JsonGenerator;

/**
 * <pre>
 * 설명 : Jsonp 전용 뷰
 * 		- callback파라미터는 [0-9A-Za-z_\\.] 허용, 이외 callback값은 허용하지 않고 jsonp값으로 응답
 * </pre>
 * @Author  : JUKI
 * @Date    : 2020. 03. 05.
 * @History
 *  이름     : 일자          : 변경내용
 * ------------------------------------------------------
 *  JUKI : 2020. 03. 05. : 신규 개발.
 */
public class JsonpViewRenderer extends MappingJackson2JsonView{
	
	private String callback = "jsonp";
	
	/**
	 * Pattern for validating jsonp callback parameter values.
	 */
	private static final Pattern CALLBACK_PARAM_PATTERN = Pattern.compile("[0-9A-Za-z_\\.]*");
	
	
	@Override
	protected void writePrefix(JsonGenerator generator, Object object) throws IOException {
		String jsonpFunction = null;
		if (object instanceof MappingJacksonValue) {
			jsonpFunction = ((MappingJacksonValue) object).getJsonpFunction();
		}
		jsonpFunction = (jsonpFunction != null) ? jsonpFunction : this.getCallback();
		generator.writeRaw( jsonpFunction + "(" );
	}

	@Override
	protected void writeSuffix(JsonGenerator generator, Object object) throws IOException {
		generator.writeRaw(");");
	}
	
	/**getter*/
	public String getCallback() {
		return callback;
	}

	/**setter*/
	public void setCallback(String callback) {
		if ( !this.isValidCallbackQueryParam(callback) ) {
			if (logger.isDebugEnabled()) {
				logger.debug("Ignoring invalid callback parameter value: " + callback);
			}
		}else{
			this.callback = callback;
		}
	}
	
	protected boolean isValidCallbackQueryParam(String value) {
		return CALLBACK_PARAM_PATTERN.matcher(value).matches();
	}
}
