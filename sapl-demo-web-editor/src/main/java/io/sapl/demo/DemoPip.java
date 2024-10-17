package io.sapl.demo;

import io.sapl.api.interpreter.Val;
import io.sapl.api.pip.Attribute;
import io.sapl.api.pip.EnvironmentAttribute;
import io.sapl.api.pip.PolicyInformationPoint;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;

@RequiredArgsConstructor
@PolicyInformationPoint(name = DemoPip.NAME, description = DemoPip.DESCRIPTION)
public class DemoPip {

    public static final String NAME = "demo";

    public static final String DESCRIPTION = "Schema Demo PIP";

    @EnvironmentAttribute(docs = "Demo", schema = """
            {  
                "$schema": "https://json-schema.org/draft/2020-12/schema",  
                "$id": "https://example.com/product.schema.json",  
                "title": "Product",  
                "description": "A product from Acme's catalog",  
                "type": "object",  
                "properties": {    
                    "productId": {      
                        "description": "The unique identifier for a product",
                        "type": "integer"    
                    },
                    "productName": {
                        "description": "Name of the product",
                        "type": "string"
                    }  
                }
            }
            """)
    public static Flux<Val> product() {
        return Flux.just(Val.TRUE);
    }
    
    @Attribute(docs = "DEmo2", schema = """
            {  
                "$schema": "https://json-schema.org/draft/2020-12/schema",  
                "$id": "https://example.com/product.schema.json",  
                "title": "Product",  
                "description": "A product from Acme's catalog",  
                "type": "object",  
                "properties": {    
                    "hinz": {      
                        "description": "The unique identifier for a product",
                        "type": "integer"    
                    },
                    "kunz": {
                        "description": "Name of the product",
                        "type": "string"
                    }  
                }
            }
            """)
    public static Flux<Val> something(Val leftHand) {
        return Flux.just(Val.TRUE);
    }
}
