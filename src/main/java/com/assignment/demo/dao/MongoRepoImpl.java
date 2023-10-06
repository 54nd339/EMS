package com.assignment.demo.dao;

import com.assignment.demo.model.Audit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;


@Repository("Mongo")
public class MongoRepoImpl implements AuditRepo {

    @Autowired
    private MongoTemplate mongoTemplate;
    
    @Override
    public void createAudit(Audit audit) {
        mongoTemplate.save(audit);
    }
}
