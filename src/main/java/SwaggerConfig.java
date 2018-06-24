import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMethod;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.builders.ResponseMessageBuilder;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Arrays;

@Configuration
@EnableSwagger2
public class SwaggerConfig {

    @Bean
    public Docket api() {
        //@formatter:off
        return new Docket(DocumentationType.SWAGGER_2)
                .useDefaultResponseMessages(false)
                .select()
                .apis(RequestHandlerSelectors.basePackage("in.develbyte.in"))
                .paths(PathSelectors.any())
                .build()
                .globalResponseMessage(RequestMethod.GET,Arrays.asList(
                        new ResponseMessageBuilder()
                                .code(404)
                                .message("Not Found")
                                .responseModel(new ModelRef("Response"))
                                .build(),
                        new ResponseMessageBuilder()
                                .code(500)
                                .message("Internal Server Error")
                                .responseModel(new ModelRef("Response"))
                                .build()))
                .globalResponseMessage(RequestMethod.POST, Arrays.asList(
                        new ResponseMessageBuilder()
                                .code(400)
                                .message("Bad Request")
                                .responseModel(new ModelRef("Response"))
                                .build(),
                        new ResponseMessageBuilder()
                                .code(500)
                                .message("Internal Server Error")
                                .responseModel(new ModelRef("Response"))
                                .build()));
        //@formatter:on
    }
}
