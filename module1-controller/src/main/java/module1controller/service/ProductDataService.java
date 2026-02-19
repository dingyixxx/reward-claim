package module1controller.service;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Random;

@Service
public class ProductDataService {

    // 商品类别和对应的价格范围
    private static final Object[][] PRODUCT_CATEGORIES = {
            {"手机数码", new String[]{"iPhone 15", "华为Mate 60", "小米14", "OPPO Find X7", "vivo X100"},
                    new BigDecimal[]{new BigDecimal("5999"), new BigDecimal("4999"), new BigDecimal("3999"), new BigDecimal("4499")}},

            {"电脑办公", new String[]{"MacBook Pro", "ThinkPad X1", "Surface Laptop", "Dell XPS"},
                    new BigDecimal[]{new BigDecimal("12999"), new BigDecimal("9999"), new BigDecimal("8999"), new BigDecimal("11999")}},

            {"服装配饰", new String[]{"Nike运动鞋", "Adidas外套", "优衣库羽绒服", "ZARA连衣裙"},
                    new BigDecimal[]{new BigDecimal("899"), new BigDecimal("599"), new BigDecimal("399"), new BigDecimal("299")}},

            {"食品饮料", new String[]{"星巴克咖啡", "三顿半咖啡", "喜茶", "奈雪的茶"},
                    new BigDecimal[]{new BigDecimal("35"), new BigDecimal("25"), new BigDecimal("28"), new BigDecimal("32")}},

            {"家居生活", new String[]{"小米扫地机器人", "戴森吸尘器", "美的空调", "海尔冰箱"},
                    new BigDecimal[]{new BigDecimal("1999"), new BigDecimal("2999"), new BigDecimal("3999"), new BigDecimal("2499")}}
    };

    private final Random random = new Random();

    // 获取随机商品信息
    public ProductInfo getRandomProduct() {
        // 随机选择商品类别
        Object[] category = PRODUCT_CATEGORIES[random.nextInt(PRODUCT_CATEGORIES.length)];
        String categoryName = (String) category[0];
        String[] products = (String[]) category[1];
        BigDecimal[] prices = (BigDecimal[]) category[2];

        // 随机选择具体商品和价格
        String productName = products[random.nextInt(products.length)];
        BigDecimal price = prices[random.nextInt(prices.length)];

        return new ProductInfo(productName, categoryName, price);
    }

    // 商品信息内部类
    public static class ProductInfo {
        private final String name;
        private final String category;
        private final BigDecimal price;

        public ProductInfo(String name, String category, BigDecimal price) {
            this.name = name;
            this.category = category;
            this.price = price;
        }

        // getters
        public String getName() { return name; }
        public String getCategory() { return category; }
        public BigDecimal getPrice() { return price; }
    }
}
