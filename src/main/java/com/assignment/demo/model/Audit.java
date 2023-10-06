package com.assignment.demo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document("audit")
public class Audit {
    @MongoId
    private String id;
    @CreationTimestamp
    private Timestamp timestamp;
    private String action;
}
