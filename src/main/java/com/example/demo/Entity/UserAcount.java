package com.example.demo.Entity;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.OneToOne;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class UserAcount{

	
	@Id
	private Long id;
	
	@OneToOne
	@JoinColumn(name = "id")
	@MapsId
	private User user;
	
	@ManyToMany()
	@JoinTable(name = "user_resources", joinColumns = @JoinColumn(name = "user_account_id"), inverseJoinColumns = @JoinColumn(name = "resource_id"))
	private Set<PResource> resources = new HashSet<PResource>();

	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
	private User owner;
}
