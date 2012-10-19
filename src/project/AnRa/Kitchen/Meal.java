package project.AnRa.Kitchen;

public class Meal {
	final private String meal_ordered_id;
	final private String meal_id;
	final private String mealName;
	final private String extraNotes;
	final private String order_id;

	public String getMealName() {
		return mealName;
	}

	public String getMealExtraNotes() {
		return extraNotes;
	}

	public String getMealID() {
		return meal_id;
	}

	public String getMealOrderedID() {
		return meal_ordered_id;
	}
	
	public String getOrderID() {
		return order_id;
	}

	public Meal(final String mOrderedId, final String m_id, final String name,
			final String extra, final String o_id) {
		meal_ordered_id = mOrderedId;
		meal_id = m_id;
		mealName = name;
		extraNotes = extra;
		order_id = o_id;
	}

	public String toString() {
		return mealName;
	}

}
