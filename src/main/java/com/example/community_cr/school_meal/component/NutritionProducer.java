package com.example.community_cr.school_meal.component;

import java.util.List;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.example.community_cr.diet.entity.Food;
import com.example.kafka_schemas.NutritionEvent;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@AllArgsConstructor
public class NutritionProducer {
	private final KafkaTemplate<String, NutritionEvent> kafkaTemplate;

	// TODO 토픽, 키 값 이너팀에 부탁해서 실제 값으로 적용하기
	public void sendAllMealItems(List<Food> foods) {
		try {
			for (Food food : foods) {
				NutritionEvent nutritionEvent = createNutritionEventFromFood(food);
				ProducerRecord<String, NutritionEvent> producerRecord = new ProducerRecord<>(
					"meal.nutrition.updated", "nutrition.data", nutritionEvent);
				kafkaTemplate.send(producerRecord);
				log.info("Kafka 전송 성공 - nutritionEvent: {}", nutritionEvent);
			}
		} catch (Exception e) {
			log.error("Kafka 전송 실패", e);
		}
	}

	private NutritionEvent createNutritionEventFromFood(Food food) {
		return new NutritionEvent(food.getFoodName(), food.getFoodWeight(), food.getKcal(), food.getProtein(),
			food.getFat(), food.getCarb());
	}
}
