package com.semi.domain.rpa.parser;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

@Service
public class JsoupTestService {

    /**
     * 네이버 쇼핑 linkUrl을 받아 최종 목적지 URL을 반환합니다.
     */
    public String getFinalDestinationUrl(String linkUrl) {
        try{
        Connection.Response response = Jsoup.connect(linkUrl)
            .userAgent(Constants.Http.USER_AGENT)
            .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8")
            .header("Accept-Language", "ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7")
            .header("Cache-Control", "no-cache")
            .header("Pragma", "no-cache")
            .referrer("https://snxbest.naver.com/")
            .ignoreHttpErrors(true) // 에러가 나도 응답 객체를 강제로 받음
            .followRedirects(false) // 중요: 자동으로 이동하지 말고 한 단계씩 끊어서 확인
                // .followRedirects(true)
            .ignoreHttpErrors(true) // 429 에러가 나도 Exception을 던지지 않고 응답 객체를 받음
            .timeout(10000)
            .execute();

        String location = response.header("Location"); 
        System.out.println("서버가 지시한 다음 주소: " + location);

        // 서버가 심으려고 했던 쿠키 확인
        Map<String, String> cookies = response.cookies();
        System.out.println("서버가 보낸 쿠키: " + cookies);



        if (response.statusCode() == 429) {
            return "Error: 과도한 요청으로 차단되었습니다. 잠시 후 시도하세요.";
        }

        return response.url().toString();
    } catch (Exception e) {
        return "Error: " + e.getMessage();
    }
    }


    public Map<String, Object> getAllAvailableInfo(String productUrl) {
        Map<String, Object> result = new HashMap<>();

        try {
            // 1. 페이지 접속 (429 방지를 위한 최소 헤더 설정)
            Connection.Response response = Jsoup.connect(productUrl)
            .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/119.0.0.0 Safari/537.36")
            .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8")
            .header("Accept-Language", "ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7")
            .header("Cache-Control", "no-cache")
            .header("Pragma", "no-cache")
            .referrer("https://snxbest.naver.com/")
            .ignoreHttpErrors(true) // 에러가 나도 응답 객체를 강제로 받음
            .followRedirects(false) // 중요: 자동으로 이동하지 말고 한 단계씩 끊어서 확인
                // .followRedirects(true)
            .ignoreHttpErrors(true) // 429 에러가 나도 Exception을 던지지 않고 응답 객체를 받음
            .timeout(10000)
            .execute();

            Document doc = response.parse();

            // 2. 기초적인 정적 데이터 추출
            result.put("pageTitle", doc.title()); // 브라우저 탭 제목
            
            // 상품명 (스마트스토어의 일반적인 클래스명 구조)
            Element productName = doc.selectFirst("h3._22_f3_ym6D, h3.prod_title"); 
            result.put("productName", productName != null ? productName.text() : "Not Found");

            // 가격
            Element price = doc.selectFirst("span._1LY7CqC1W1, .price_real");
            result.put("price", price != null ? price.text() : "Not Found");

            // 3. 숨겨진 JSON 데이터 (핵심)
            // 스마트스토어는 상세 데이터를 window.__PRELOADED_STATE__ 에 담아둡니다.
            Elements scripts = doc.select("script");
            for (Element script : scripts) {
                String data = script.data();
                if (data.contains("window.__PRELOADED_STATE__")) {
                    // 데이터가 너무 크므로 앞부분만 잘라서 확인하거나 전체를 보관
                    // 실제로는 여기서 Jackson/Gson으로 JSON 파싱을 하면 모든 정보를 객체로 다룰 수 있습니다.
                    result.put("rawJsonDataPreview", data.substring(0, Math.min(data.length(), 500)) + "...");
                    result.put("hasFullJson", true);
                    break;
                }
            }

            // 4. 모든 메타 데이터 (SEO 정보)
            Map<String, String> metaTags = new HashMap<>();
            Elements metas = doc.select("meta[property^=og:]"); // OpenGraph 데이터
            for (Element meta : metas) {
                metaTags.put(meta.attr("property"), meta.attr("content"));
            }
            result.put("metaData", metaTags);

        } catch (Exception e) {
            result.put("error", e.getMessage());
        }

        return result;
    }



    
public String getJsonFromHtml(String pageUrl) {
    try {
            // Connection.Response response = Jsoup.connect(pageUrl) ;
            Connection.Response response = Jsoup.connect(pageUrl)
            .userAgent(Constants.Http.USER_AGENT)
            .header("Accept", "text/html,application/xhtml+xml")
            .header("Accept-Language", "ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7")
            // .header("Cache-Control", "no-cache")
            // .header("Pragma", "no-cache")
            .referrer("https://snxbest.naver.com/")
            // .ignoreHttpErrors(true) // 에러가 나도 응답 객체를 강제로 받음
            // .followRedirects(false) // 중요: 자동으로 이동하지 말고 한 단계씩 끊어서 확인
                // .followRedirects(true)
            // .ignoreHttpErrors(true) // 429 에러가 나도 Exception을 던지지 않고 응답 객체를 받음
            .timeout(10000)
            .execute();

            System.err.println("HTTP 응답 상태 코드: " + response.statusCode());
            System.err.println("HTTP 응답 본문: " + response.body());
        return response.statusCode() + "|" + response.body();

    } catch (Exception e) {
        e.printStackTrace();
    }
    return null;
}


}