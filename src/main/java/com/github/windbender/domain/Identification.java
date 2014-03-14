package com.github.windbender.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.joda.time.DateTime;

@Entity
@Table(name="identifications")
public class Identification {
	
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name = "id", nullable=false)
	private int id;
	
	@ManyToOne
    @JoinColumn(name="user_id", nullable=false)
    private User identifier;
	
	@Column(name="identificationTime", nullable=false)
	DateTime timeOfIdentification;
	
	@ManyToOne
    @JoinColumn(name="image_event_id", nullable=true)
    ImageEvent identifiedEvent;
	
	public ImageEvent getIdentifiedEvent() {
		return identifiedEvent;
	}

	public void setIdentifiedEvent(ImageEvent identifiedEvent) {
		this.identifiedEvent = identifiedEvent;
	}

	@ManyToOne
    @JoinColumn(name="image_id", nullable=true)
	ImageRecord identifiedImage;

	
	@ManyToOne
	@JoinColumn(name="species_id", nullable=false)
	Species speciesIdentified;
	
	@Column(name="number", nullable=false)
	int numberOfIndividuals;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public User getIdentifier() {
		return identifier;
	}

	public void setIdentifier(User identifier) {
		this.identifier = identifier;
	}

	public DateTime getTimeOfIdentification() {
		return timeOfIdentification;
	}

	public void setTimeOfIdentification(DateTime timeOfIdentification) {
		this.timeOfIdentification = timeOfIdentification;
	}

	public ImageRecord getIdentifiedImage() {
		return identifiedImage;
	}

	public void setIdentifiedImage(ImageRecord identifiedImage) {
		this.identifiedImage = identifiedImage;
	}

	public Species getSpeciesIdentified() {
		return speciesIdentified;
	}

	public void setSpeciesIdentified(Species speciesIdentified) {
		this.speciesIdentified = speciesIdentified;
	}

	public int getNumberOfIndividuals() {
		return numberOfIndividuals;
	}

	public void setNumberOfIndividuals(int numberOfIndividuals) {
		this.numberOfIndividuals = numberOfIndividuals;
	}

	@Override
	public String toString() {
		return "Identification [id=" + id + ", identifier=" + identifier
				+ ", timeOfIdentification=" + timeOfIdentification
				+ ", identifiedImage=" + identifiedImage
				+ ", speciesIdentified=" + speciesIdentified
				+ ", numberOfIndividuals=" + numberOfIndividuals + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		result = prime * result
				+ ((identifiedImage == null) ? 0 : identifiedImage.hashCode());
		result = prime * result
				+ ((identifier == null) ? 0 : identifier.hashCode());
		result = prime * result + numberOfIndividuals;
		result = prime
				* result
				+ ((speciesIdentified == null) ? 0 : speciesIdentified
						.hashCode());
		result = prime
				* result
				+ ((timeOfIdentification == null) ? 0 : timeOfIdentification
						.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Identification other = (Identification) obj;
		if (id != other.id)
			return false;
		if (identifiedImage == null) {
			if (other.identifiedImage != null)
				return false;
		} else if (!identifiedImage.equals(other.identifiedImage))
			return false;
		if (identifier == null) {
			if (other.identifier != null)
				return false;
		} else if (!identifier.equals(other.identifier))
			return false;
		if (numberOfIndividuals != other.numberOfIndividuals)
			return false;
		if (speciesIdentified == null) {
			if (other.speciesIdentified != null)
				return false;
		} else if (!speciesIdentified.equals(other.speciesIdentified))
			return false;
		if (timeOfIdentification == null) {
			if (other.timeOfIdentification != null)
				return false;
		} else if (!timeOfIdentification.equals(other.timeOfIdentification))
			return false;
		return true;
	}
	
	
	
}
