package org.crossfit.app.domain.util;

import java.io.IOException;

import javax.inject.Inject;

import org.crossfit.app.service.CrossFitBoxSerivce;
import org.crossfit.app.service.TimeService;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

/**
 * Custom Jackson serializer for transforming a Joda DateTime object to JSON.
 */
public class CustomDateTimeSerializer extends JsonSerializer<DateTime> {

    private static DateTimeFormatter formatter = DateTimeFormat
            .forPattern("yyyy-MM-dd'T'HH:mm:ss");


    @Inject
    private CrossFitBoxSerivce boxService;
    
    @Inject
    private TimeService timeService;
    
    public CustomDateTimeSerializer() {
		super();        
		SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);    
	}



	@Override
    public void serialize(DateTime value, JsonGenerator generator,
                          SerializerProvider serializerProvider)
            throws IOException {
        generator.writeString(formatter.print(value.toDateTime(timeService.getDateTimeZone(boxService.findCurrentCrossFitBox()))));
    }

}
