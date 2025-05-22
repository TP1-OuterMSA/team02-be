package com.example.community_cr.mealMatch.match.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
	name = "place",
	uniqueConstraints = {
		@UniqueConstraint(columnNames = {"longitude", "latitude", "address"})
	}
)
@Builder
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Place {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String name;
	private double latitude;
	private double longitude;
	private String address;

	@Builder.Default
	@OneToMany(mappedBy = "place", fetch = FetchType.LAZY)
	private List<MatchPost> matchPostList = new ArrayList<>();

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof Place place))
			return false;
		return Objects.equals(id, place.id);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}
}
