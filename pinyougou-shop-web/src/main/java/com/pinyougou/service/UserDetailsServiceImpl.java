package com.pinyougou.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.pinyougou.pojo.TbSeller;
import com.pinyougou.sellergoods.service.SellerService;

/**
 * 认证类
 * @author csl
 *
 */
public class UserDetailsServiceImpl implements UserDetailsService {
	
	private SellerService sellerService;
	public void setSellerService(SellerService sellerService) {
		this.sellerService = sellerService;
	}


	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		System.out.println("经过了UserDetailsServiceImpl....");
		//构建角色列表
		List<GrantedAuthority> grantedAuths = new ArrayList<GrantedAuthority>();
		//根据用户名查询密码
		TbSeller seller = sellerService.findSeller(username);
		if(seller!=null&&!seller.getStatus().equals("0")) {
			grantedAuths.add(new SimpleGrantedAuthority("ROLE_SELLER"));
			return new User(username,seller.getPassword(),grantedAuths);
		}else {
			return null;
		}
		
	}

}
