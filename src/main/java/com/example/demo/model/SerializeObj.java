package com.example.demo.model;

import jakarta.persistence.*;

import java.io.*;

@Entity
@Table(name = "SerializeObjs")
public class SerializeObj {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "content")
    private String content;

    @Column(name = "serialized_content")
    private byte[] serializedContent;

    public SerializeObj() {};

    public SerializeObj(String content, SerializedObj2 obj) {
        this.content = content;
        this.serializedContent = serializeContent(obj);
    }

    public SerializeObj(String content, SerializedObj obj) {
        this.content = content;
        this.serializedContent = serializeContent(obj);
    }

    private byte[] serializeContent(Object obj) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream out = null;
        try {
            out = new ObjectOutputStream(bos);
            out.writeObject(obj);
            return bos.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                bos.close();
            } catch (IOException ex) {
                // ignore close exception
            }
        }
        return new byte[0];
    }

    private Object deserializeContent() {
        ByteArrayInputStream bis = new ByteArrayInputStream(serializedContent);
        try (ObjectInput in = new ObjectInputStream(bis)) {

            return in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        // ignore close exception
        return null;
    }

    public int getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

    public byte[] getSerializedContentBytes() {
        return serializedContent;
    }

    public SerializedObj2 getSerializedObj2() {
        return (SerializedObj2) deserializeContent();
    }

    public SerializedObj getSerializedObj() {
        return (SerializedObj) deserializeContent();
    }
}