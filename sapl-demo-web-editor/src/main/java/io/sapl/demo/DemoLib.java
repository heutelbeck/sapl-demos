package io.sapl.demo;

import io.sapl.api.functions.Function;
import io.sapl.api.functions.FunctionLibrary;
import io.sapl.api.interpreter.Val;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@FunctionLibrary(name = DemoLib.NAME, description = DemoLib.DESCRIPTION)
public class DemoLib {

    public static final String NAME = "demo";

    public static final String DESCRIPTION = "Schema Demo Lib";

    @Function(docs = "Demo", schema = """
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
    public static Val prod(Val source) {
        return source;
    }
}
