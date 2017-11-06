package com.centerspin.rest.app;
import com.centerspin.utils.Constants;
import java.util.*;
import org.json.*;

public class VoteCalculator {

            
    public static void processVotes(JSONObject articleData, JSONArray articleVotes) {
        
        // Recalculate each metric
        for (String biasMetric : Constants.BIAS_METRIC_KEYS) {

            int sum = 0;
            for (int i = 0; i < articleVotes.length(); i++) {
                int rawVote = articleVotes.getJSONObject(i).getJSONObject(Constants.biasMetrics).getInt(biasMetric);
                sum += rawVote;
            }

            float metricAverage = (float)sum/(float)articleVotes.length();

            // Update article metric value
            articleData.getJSONObject(Constants.biasMetrics).put(biasMetric, metricAverage);

        }
        
        // Calculate "averages" 
        float sumOfMetrics = 0;
        for (String biasMetric : Constants.BIAS_METRIC_KEYS) {
            sumOfMetrics += articleData.getJSONObject(Constants.biasMetrics).getDouble(biasMetric);
        }

        float avgMetric = sumOfMetrics/(float)Constants.BIAS_METRIC_KEYS.length;
        float score = (avgMetric/Constants.MAX_METRIC_VAL) * 100;

        articleData.getJSONObject(Constants.biasMetrics).put(Constants.avgRating, avgMetric);
        articleData.getJSONObject(Constants.biasMetrics).put(Constants.score, score);
    }
    
}
