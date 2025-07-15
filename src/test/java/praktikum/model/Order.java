package praktikum.model;

import java.util.List;

public class Order {
        public String firstName;
        public String lastName;
        public String address;
        public String metroStation;
        public String phone;
        public int rentTime;
        public String deliveryDate;
        public String comment;
        public List<String> color;

        public Order(String firstName, String lastName, String address, String metroStation,
                     String phone, int rentTime, String deliveryDate, String comment, List<String> color) {
                this.firstName = firstName;
                this.lastName = lastName;
                this.address = address;
                this.metroStation = metroStation;
                this.phone = phone;
                this.rentTime = rentTime;
                this.deliveryDate = deliveryDate;
                this.comment = comment;
                this.color = color;
        }

        public static Order defaultOrder(List<String> color) {
                return new Order(
                        "Иван",
                        "Ветров",
                        "Москва, улица Охотный ряд, 33",
                        "4",
                        "+7 900 555 35 35",
                        5,
                        "2025-07-17",
                        "Без комментариев",
                        color
                );
        }
}
