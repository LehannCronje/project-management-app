package com.example.demo.Entity;

import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * User
 */

@Entity
@Table(name = "users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User implements UserDetails {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	Long Id;

	@Column(unique = true)
	@NotEmpty
	private String username;

	@NotEmpty
	private String password;

	@Builder.Default
	private String isActive = "ACTIVE";

	@Builder.Default
	private boolean updated = false;

	@ElementCollection(fetch = FetchType.EAGER)
	@Builder.Default
	private List<String> roles = new ArrayList<>();

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return this.roles.stream().map(SimpleGrantedAuthority::new).collect(toList());
	}

	// custom relations

	@ManyToMany()
	@JoinTable(name = "user_projects", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "project_id"))
	private Set<Project> projects = new HashSet<Project>();

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
	private Set<MpxFile> files = new HashSet<MpxFile>();

	@JsonIgnore
	@OneToOne(mappedBy = "user")
	private UserAcount userAcount;

	@JsonIgnore
	@OneToMany(mappedBy = "owner", cascade = CascadeType.ALL)
	private Set<UserAcount> userAcounts = new HashSet<UserAcount>();

	public void removeMpxFile(MpxFile file) {
		files.remove(file);
		file.setUser(null);
	}

	public void removeProject(Project project) {
		this.projects.remove(project);
	}

	// end

	@Override
	public String getPassword() {
		return this.password;
	}

	@Override
	public String getUsername() {
		return this.username;
	}

	@Override
	public boolean isAccountNonExpired() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isEnabled() {
		if (this.isActive == null)
			return false;
		if (!this.isActive.equals("ACTIVE"))
			return false;

		return true;
	}

	public void setIsUpdated(boolean value) {
		this.updated = value;
	}
}