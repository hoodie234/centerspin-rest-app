package com.centerspin.app;

public class ArticleSearchSpec {

    public String type;
    public String topic;
    public String sortBy;

    public ArticleSearchSpec() {
    }


    public boolean equals(ArticleSearchSpec obj) {
        
        if (type.equals(obj.type) == false) return false;
        if (topic.equals(obj.topic) == false) return false;
        if (sortBy.equals(obj.sortBy) == false) return false;
        
        return true;
    }
    
    
}
