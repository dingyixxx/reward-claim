package com.example;

import io.jsonwebtoken.*;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.*;

public class AppJwtUtil {

    // TOKEN的有效期一天（S）
    private static final int TOKEN_TIME_OUT = 3_600;
    // 加密KEY
    private static final String TOKEN_ENCRY_KEY = "MDk4ZjZiY2Q0NjIxZDM3M2NhZGU0ZTgzMjYyN2I0ZjYASdhkjhaskdasdasdhjhkjasdhkasdhkjhsakjdhkjashdjk21i21eh32kenkjaxnwekjaheclkHEKJCNSJNDAKCNDXLKANDKJncdhewiury7ewyr8qwyriqwjfdmksmsfz.mfdfadfafc84nrc1or472i73bv352c7nnximinikucyueuwbrcyuewbycwr424v23v5235n29v8n35o832co7b7bc4234c.c42434.r43vr2cr2.c23ce3ce23niniweniqnweinxqieiwqnei87y878y82yr824yr827yr8ye8y7y87w81ye87y81y7e813e.dqxeqwexqwcewqeMDk4ZjZiY2Q0NjIxZDM3M2NhZGU0ZTgzMjYyN2I0ZjYASdhkjhaskdasdasdhjhkjasdhkasdhkjhsakjdhkjashdjk21i21eh32kenkjaxnwekjaheclkHEKJCNSJNDAKCNDXLKANDKJncdhewiury7ewyr8qwyriqwjfdmksmsfz.mfdfadfafc84nrc1or472i73bv352c7nnximinikucyueuwbrcyuewbycwr424v23v5235n29v8n35o832co7b7bc4234c.c42434.r43vr2cr2.c23ce3ce23niniweniqnweinxqieiwqnei87y878y82yr824yr827yr8ye8y7y87w81ye87y81y7e813e.dqxeqwexqwcewqe";
    // 最小刷新间隔(S)
    private static final int REFRESH_TIME = 300;

    // 生产ID
    public static String getToken(Long id) {
        Map<String, Object> claimMaps = new HashMap<>();
        claimMaps.put("id", id);
        long currentTime = System.currentTimeMillis();
        return Jwts.builder()
                .setId(UUID.randomUUID().toString())
                .setIssuedAt(new Date(currentTime))  //签发时间
                .setSubject("system")  //说明
                .setIssuer("dingyi") //签发者信息
                .setAudience("app")  //接收用户
                .compressWith(CompressionCodecs.GZIP)  //数据压缩方式
                .signWith(SignatureAlgorithm.HS512, generalKey()) //加密方式
                .setExpiration(new Date(currentTime + TOKEN_TIME_OUT * 1000))  //过期时间戳
                .addClaims(claimMaps) //cla信息
                .compact();
    }

    /**
     * 获取token中的claims信息
     *
     * @param token
     * @return
     */
    private static Jws<Claims> getJws(String token) {
        return Jwts.parser()
                .setSigningKey(generalKey())
                .parseClaimsJws(token);
    }

    /**
     * 获取payload body信息
     *
     * @param token
     * @return
     */
    public static Claims getClaimsBody(String token) throws ExpiredJwtException {
        return getJws(token).getBody();
    }

    /**
     * 获取hearder body信息
     *
     * @param token
     * @return
     */
    public static JwsHeader getHeaderBody(String token) {
        return getJws(token).getHeader();
    }

    /**
     * 是否过期
     *
     * @param claims
     * @return -1：有效，0：有效，1：过期，2：过期
     */
    public static int verifyToken(Claims claims) throws Exception {
        if (claims == null) {
            return 1;
        }

        claims.getExpiration().before(new Date());
        // 需要自动刷新TOKEN
        if ((claims.getExpiration().getTime() - System.currentTimeMillis()) > REFRESH_TIME * 1000) {
            return -1;
        } else {
            return 0;
        }
    }

    /**
     * 由字符串生成加密key
     *
     * @return
     */
    public static SecretKey generalKey() {
        // 直接使用原始密钥字符串的字节，不需要Base64编码
        byte[] encodedKey = TOKEN_ENCRY_KEY.getBytes();
        // 使用 HMAC SHA-512 算法创建密钥
        SecretKey key = new SecretKeySpec(encodedKey, SignatureAlgorithm.HS512.getJcaName());
        return key;
    }

    public static void main(String[] args) {
       /* Map map = new HashMap();
        map.put("id","11");*/
        System.out.println(AppJwtUtil.getToken(1102L));
        Jws<Claims> jws = AppJwtUtil.getJws("eyJhbGciOiJIUzUxMiIsInppcCI6IkdaSVAifQ.H4sIAAAAAAAAADWLQQqEMAwA_5KzhURNt_qb1KZYQSi0wi6Lf9942NsMw3zh6AVW2DYmDGl2WabkZgreCaM6VXzhFBfJMcMARTqsxIG9Z888QLui3e3Tup5Pb81013KKmVzJTGo11nf9n8v4nMUaEY73DzTabjmDAAAA.4SuqQ42IGqCgBai6qd4RaVpVxTlZIWC826QA9kLvt9d-yVUw82gU47HDaSfOzgAcloZedYNNpUcd18Ne8vvjQA");
        Claims claims = jws.getBody();
        System.out.println(claims.get("id"));

    }

}
