package org.msgpack.jackson.dataformat.msgpack.msgpack;

import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.core.type.ResolvedType;
import com.fasterxml.jackson.core.type.TypeReference;

import java.io.IOException;
import java.util.Iterator;

public class MessagePackCodec extends ObjectCodec {
    @Override
    public <T> T readValue(JsonParser jp, Class<T> valueType) throws IOException, JsonProcessingException {
        System.out.println("readValue#1");
        return null;
    }

    @Override
    public <T> T readValue(JsonParser jp, TypeReference<?> valueTypeRef) throws IOException, JsonProcessingException {
        System.out.println("readValue(JsonParser jp, Class<T> valueType)");
        return null;
    }

    @Override
    public <T> T readValue(JsonParser jp, ResolvedType valueType) throws IOException, JsonProcessingException {
        System.out.println("readValue(JsonParser jp, Class<T> valueType)");
        return null;
    }

    @Override
    public <T> Iterator<T> readValues(JsonParser jp, Class<T> valueType) throws IOException, JsonProcessingException {
        System.out.println("readValue(JsonParser jp, Class<T> valueType)");
        return null;
    }

    @Override
    public <T> Iterator<T> readValues(JsonParser jp, TypeReference<?> valueTypeRef) throws IOException, JsonProcessingException {
        System.out.println("readValue(JsonParser jp, Class<T> valueType)");
        return null;
    }

    @Override
    public <T> Iterator<T> readValues(JsonParser jp, ResolvedType valueType) throws IOException, JsonProcessingException {
        System.out.println("readValue(JsonParser jp, Class<T> valueType)");
        return null;
    }

    @Override
    public void writeValue(JsonGenerator jgen, Object value) throws IOException, JsonProcessingException {
        System.out.println("readValue(JsonParser jp, Class<T> valueType)");
    }

    @Override
    public <T extends TreeNode> T readTree(JsonParser jp) throws IOException, JsonProcessingException {
        System.out.println("readValue(JsonParser jp, Class<T> valueType)");
        return null;
    }

    @Override
    public void writeTree(JsonGenerator jg, TreeNode tree) throws IOException, JsonProcessingException {
        System.out.println("readValue(JsonParser jp, Class<T> valueType)");

    }

    @Override
    public TreeNode createObjectNode() {
        System.out.println("readValue(JsonParser jp, Class<T> valueType)");
        return null;
    }

    @Override
    public TreeNode createArrayNode() {
        System.out.println("readValue(JsonParser jp, Class<T> valueType)");
        return null;
    }

    @Override
    public JsonParser treeAsTokens(TreeNode n) {
        System.out.println("readValue(JsonParser jp, Class<T> valueType)");
        return null;
    }

    @Override
    public <T> T treeToValue(TreeNode n, Class<T> valueType) throws JsonProcessingException {
        System.out.println("readValue(JsonParser jp, Class<T> valueType)");
        return null;
    }

    @Override
    public JsonFactory getJsonFactory() {
        System.out.println("readValue(JsonParser jp, Class<T> valueType)");
        return null;
    }
}
