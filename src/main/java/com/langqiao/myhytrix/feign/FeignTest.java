package com.langqiao.myhytrix.feign;


import java.util.List;

import feign.Feign;
import feign.Param;
import feign.RequestLine;
import feign.gson.GsonDecoder;

public class FeignTest {

	public static void main(String[] args) {
		GitHub github = Feign.builder()
                .decoder(new GsonDecoder())
                .target(GitHub.class, "https://api.github.com");
                	
		// 获取贡献者列表，并打印其登录名以及贡献次数
		List<Contributor> contributors = github.contributors("netflix", "feign");
		for (Contributor contributor : contributors) {
			System.out.println(contributor.login + " (" + contributor.contributions + ")");
		}
	}
}

class Contributor {
	String login;
	int contributions;
}

interface GitHub {
	
	@RequestLine("GET /repos/{owner}/{repo}/contributors")
	List<Contributor> contributors(@Param("owner") String owner,@Param("repo") String repo);
}
