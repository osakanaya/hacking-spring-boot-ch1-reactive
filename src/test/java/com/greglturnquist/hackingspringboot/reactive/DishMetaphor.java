package com.greglturnquist.hackingspringboot.reactive;

import reactor.core.publisher.Flux;

public class DishMetaphor {
	static class Dish {
		private String description;
		private boolean delivered = false;

		public static Dish deliver(Dish dish) {
			Dish deliveredDish = new Dish(dish.description);
			deliveredDish.delivered = true;

			return deliveredDish;
		}

		Dish(String description) {
			this.description = description;
		}

		public boolean isDelivered() {
			return delivered;
		}

		@Override
		public String toString() {
			return "Dish{" + //
					"description='" + description + '\'' + //
					", delivered=" + delivered + '}';
		}

	}

	static class KitchenService {
		Flux<Dish> getDishes() {
			return Flux.just(
				new Dish("Sesame chicken"),
				new Dish("Lo main noodles, plain"),
				new Dish("Sweet & sour beef")
			);
		}
	}
	
	static class SimpleServer {
		private final KitchenService kitchen;
		
		public SimpleServer(KitchenService kitchen) {
			this.kitchen = kitchen;
		}
		
		Flux<Dish> doingMyJob() {
			return this.kitchen.getDishes().map(dish -> Dish.deliver(dish));
		}
	}
	
	static class PoliteServer {
		private final KitchenService kitchen;
		
		public PoliteServer(KitchenService kitchen) {
			this.kitchen = kitchen;
		}
		
		Flux<Dish> doingMyJob() {
			return this.kitchen.getDishes()
				.doOnNext(dish -> System.out.println("Thank you for " + dish + "!"))
				.doOnError(error -> System.out.println("So sorry about " + error.getMessage()))
				.doOnComplete(() -> System.out.println("Thanks for all your hard work!"))
				.map(Dish::deliver);
		}
	}
	
	static class PoliteRestaurant {
		public static void main(String... args) {
//			SimpleServer server = new SimpleServer(new KitchenService());
			PoliteServer server = new PoliteServer(new KitchenService());
			
			server.doingMyJob().subscribe(
				dish -> System.out.println("Consuming " + dish),
				throwable -> System.err.print(throwable)
			);
		}
	}
}
