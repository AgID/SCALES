package net.scales.dispatcher.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="SUBSCRIPTIONS")
public class SubscriptionEntity {

    @Id
	@GeneratedValue
	@Column(name = "ID", nullable = false)
    private Long id;

    @Column(name="EVENT", length = 250, nullable = false)
    private String event;
    
    @Column(name="URL", length = 250, nullable = false)
	private String url;

	public SubscriptionEntity() {}

	public SubscriptionEntity(String event, String url) {
		this.event = event;
		this.url = url;
	}

    public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getEvent() {
		return event;
	}

	public void setEvent(String event) {
		this.event = event;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

}