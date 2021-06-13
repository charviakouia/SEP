package de.dedede.model.data.dtos;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

// @Beacon @Note alternatively, make this flat and inline MediumAttribute (removing it)

public abstract class MediumSearchCriterion {

	public static final class Attribute extends MediumSearchCriterion {

		private MediumAttribute value;

		public Attribute(MediumAttribute value) {
			this.value = value;
		}

		public MediumAttribute getValue() {
			return value;
		}

		public void setValue(MediumAttribute value) {
			this.value = value;
		}

		@Override
		public String toString() {
			return value.toString();
		}

		@Override
		public <Result> Result accept(Visitor<Result> visitor) {
			return visitor.visitAttribute(value);
		}

	}

	public static final class Category extends MediumSearchCriterion {

		@Override
		public String toString() {
			return "category";
		}

		@Override
		public <Result> Result accept(Visitor<Result> visitor) {
			return visitor.visitCategory();
		}
	}

	public static final class Signature extends MediumSearchCriterion {
		
		@Override
		public String toString() {
			return "signature";
		}

		@Override
		public <Result> Result accept(Visitor<Result> visitor) {
			return visitor.visitSignature();
		}
	}

	public static interface Visitor<Result> {

		Result visitAttribute(MediumAttribute attribute);

		Result visitCategory();
		
		Result visitSignature();

	}

	public static List<MediumSearchCriterion> values() {
		return Stream.concat(Stream.of(MediumAttribute.values()).map(Attribute::new), Stream.of(new Category()))
				.collect(Collectors.toList());
	}

	public static MediumSearchCriterion parse(String input) {
		return switch (input) {
		case "category" -> new Category();
		// case "signature" -> new Signature();
		default -> {
			final var attribute = MediumAttribute.parse(input);
			yield attribute == null ? null : new Attribute(attribute);
		}
		};
	}

	public abstract <Result> Result accept(Visitor<Result> visitor);
}
