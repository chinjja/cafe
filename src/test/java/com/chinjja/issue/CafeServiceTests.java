package com.chinjja.issue;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.chinjja.issue.domain.Cafe;
import com.chinjja.issue.domain.CafeMember;
import com.chinjja.issue.domain.Category;
import com.chinjja.issue.domain.LikeCount;
import com.chinjja.issue.domain.Category.Type;
import com.chinjja.issue.domain.Comment;
import com.chinjja.issue.domain.Likable;
import com.chinjja.issue.domain.Post;
import com.chinjja.issue.domain.User;
import com.chinjja.issue.form.CafeForm;
import com.chinjja.issue.form.CategoryForm;
import com.chinjja.issue.form.CommentForm;
import com.chinjja.issue.form.JoinCafeForm;
import com.chinjja.issue.form.PostForm;
import com.chinjja.issue.service.CafeService;
import com.chinjja.issue.service.UserService;

import lombok.val;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class CafeServiceTests {
	@Autowired EntityManager em;
	@Autowired UserService userService;
	@Autowired CafeService cafeService;
	
	static final String username = "admin";
	
	@Nested
	class WithUser {
		User owner;
		
		User new_user(String username, String password) {
			return userService.create(User.builder()
					.username(username)
					.password(password)
					.build());
		}
		
		@BeforeEach
		void createCafe() {
			owner = userService.byUsername(username);
		}
		
		@Test
		void shouldFailWithoutId() {
			assertThrows(Throwable.class, () -> {
				new_cafe("", "The cafe");
				em.flush();
			});
		}
		
		@Test
		void shouldFailWithoutName() {
			assertThrows(Throwable.class, () -> {
				new_cafe("cafe", "");
				em.flush();
			});
		}
		
		@Test
		void shouldFailWithDuplicatedId() {
			new_cafe("cafe", "The cafe1");
			em.flush();
			
			assertThrows(Throwable.class, () -> {
				new_cafe("cafe", "The cafe2");
				em.flush();
			});
		}
		
		@Test
		void shouldCreateCafeWithSameName() {
			new_cafe("cafe1", "The cafe1");
			new_cafe("cafe2", "The cafe1");
			em.flush();
		}
		
		Cafe new_cafe(String id, String name) {
			val form = new CafeForm();
			form.setDescription("this cafe is public");
			form.setId(id);
			form.setName(name);
			form.setNeedApproval(false);
			form.setPrivacy(false);
			
			return cafeService.createCafe(form, owner);
		}
		
		@Nested
		class WithPublicCafe {
			Cafe cafe;
			
			@BeforeEach
			void create() {
				cafe = new_cafe("cafe", "The cafe");
				em.flush();
			}
			
			@Test
			void property() {
				assertNotNull(cafe);
				assertEquals(cafe, cafeService.getCafeById(cafe.getId()));
				assertEquals(1, cafeService.countPublicCafeList());
				assertTrue(cafeService.getPublicCafeList().iterator().hasNext());
				assertEquals("cafe", cafe.getId());
				assertFalse(cafe.isPrivacy());
				assertNotNull(cafe.getOwner());
			}
			
			@Test
			void deleteCafe() {
				em.flush();
				cafeService.deleteCafe(cafe);
				assertNull(cafeService.getCafeById(cafe.getId()));
			}
			
			Category new_root_directory() {
				val form = new CategoryForm();
				form.setName("category");
				form.setParentCategoryId(null);
				form.setType(Type.DIRECTORY);
				return cafeService.createCategory(cafe, form);
			}
			
			@Nested
			class WithDirectory {
				Category directory;
				
				@BeforeEach
				void create() {
					directory = new_root_directory();
					em.flush();
				}
				
				@Test
				void property() {
					assertNotNull(directory);
					assertEquals(directory, cafeService.getCategoryById(directory.getId()));
					assertEquals(Category.Type.DIRECTORY, directory.getType());
					assertTrue(directory.isDirectory());
					assertFalse(directory.isPost());
					assertNull(directory.getParent());
				}
				
				@Test
				void deleteDirectory() {
					em.flush();
					cafeService.deleteCategory(directory);
					assertNull(cafeService.getCategoryById(directory.getId()));
				}
				
				@Test
				void cascadeDelete() {
					deleteCafe();
				}
				
				Category new_category() {
					val form = new CategoryForm();
					form.setName("post");
					form.setParentCategoryId(directory.getId());
					form.setType(Type.POST);
					return cafeService.createCategory(cafe, form);
				}
				
				@Nested
				class WithPosts {
					Category category;
					
					@BeforeEach
					void create() {
						category = new_category();
						em.flush();
					}
					
					@Test
					void property() {
						assertNotNull(category);
						assertEquals(category, cafeService.getCategoryById(category.getId()));
						assertEquals(Category.Type.POST, category.getType());
						assertFalse(category.isDirectory());
						assertTrue(category.isPost());
						assertEquals(directory, category.getParent());
					}
					
					@Test
					void delete() {
						em.flush();
						cafeService.deleteCategory(category);
						assertNull(cafeService.getCategoryById(category.getId()));
					}
					
					@Test
					void cascadeDelete() {
						deleteCafe();
					}
					
					Post new_post() {
						val form = new PostForm();
						form.setTitle("post1");
						form.setContents("post1's content");
						return cafeService.createPost(owner, category, form);
					}
					
					@Nested
					class WithPost {
						Post post;
						
						@BeforeEach
						void create() {
							post = new_post();
							em.flush();
						}
						
						@Test
						void property() {
							assertNotNull(post);
							assertEquals(post, cafeService.getPostById(post.getId()));
							assertEquals("post1", post.getTitle());
							assertEquals("post1's content", post.getContents());
							assertEquals(category, post.getCategory());
							assertEquals(0, post.getComments().size());
							assertEquals(0, post.getLikeCount());
							assertEquals(0, post.getLikes().size());
							assertEquals(0, post.getViewCount());
						}
						
						@Test
						void delete() {
							em.flush();
							cafeService.deletePost(post);
							assertNull(cafeService.getPostById(post.getId()));
						}
						
						@Test
						void cascadeDelete() {
							deleteCafe();
						}
						
						@Test
						void shouldFailWithoutCommentText() {
							val form = new CommentForm();
							cafeService.createComment(owner, post, form);
							assertThrows(Throwable.class, () -> {
								em.flush();
							});
						}
						
						LikeCount new_like_count() {
							return cafeService.createLikeCount(owner, post);
						}
						
						Comment new_comment(Likable likable) {
							val form = new CommentForm();
							form.setComment("hi");
							return cafeService.createComment(owner, likable, form);
						}
						
						@Nested
						class WithLike {
							LikeCount likeCount;
							
							@BeforeEach
							void create() {
								likeCount = new_like_count();
								em.flush();
							}
							
							@Test
							void shouldExist() {
								assertTrue(cafeService.isLiked(likeCount));
							}
							
							@Test
							void shouldNotExistAfterToggle() {
								cafeService.toggleLikeCount(likeCount);
								assertFalse(cafeService.isLiked(likeCount));
							}
							
							@Test
							void shouldNotExistAfterDelete() {
								cafeService.deleteLikeCount(likeCount);
								assertFalse(cafeService.isLiked(likeCount));
							}
							
							@Test
							void cascadeDelete() {
								deleteCafe();
							}
						}
						
						@Nested
						class WithComment {
							Comment comment;
							
							@BeforeEach
							void create() {
								comment = new_comment(post);
								em.flush();
							}
							
							@Test
							void shouldHave1CommentInPost() {
								assertNotNull(comment);
								em.flush();
								em.clear();
								val load_post = cafeService.getPostById(post.getId());
								assertEquals(1, load_post.getComments().size());
							}
							
							@Test
							void shouldHave3CommentInPost() {
								new_comment(post);
								new_comment(post);
								em.flush();
								em.clear();
								val load_post = cafeService.getPostById(post.getId());
								assertEquals(3, load_post.getComments().size());
							}
							
							@Test
							void shouldHava2CommentInParentComment() {
								new_comment(comment);
								new_comment(comment);
								em.flush();
								em.clear();
								val load_comment = cafeService.getCommentById(comment.getId());
								assertEquals(2, load_comment.getComments().size());
							}
							
							@Test
							void delete() {
								em.flush();
								cafeService.deleteComment(comment);
								assertNull(cafeService.getCommentById(comment.getId()));
							}
							
							@Test
							void cascadeDelete() {
								deleteCafe();
							}
						}
					}
				}
			}
			
			@Test
			void shouldFailWithoutGreeting() {
				val form = new JoinCafeForm();
				form.setGreeting("");
				assertThrows(Throwable.class, () -> {
					cafeService.joinCafe(cafe, owner, form);
					em.flush();
				});
			}
			
			@Test
			void shouldHaveEmptyMember() {
				em.flush();
				em.clear();
				val load_cafe = cafeService.getCafeById(cafe.getId());
				assertEquals(0, load_cafe.getMemberCount());
				assertTrue(load_cafe.getMembers().isEmpty());
			}
			
			@Test
			void onwerCannotBeMember() {
				assertThrows(Throwable.class, () -> {
					val form = new JoinCafeForm();
					form.setGreeting("hi");
					cafeService.joinCafe(cafe, owner, form);
				});
			}
			
			@Test
			void shouldBeOwnerAtThisCafe() {
				assertTrue(cafeService.isOwner(cafe, owner));
				assertTrue(cafeService.isJoined(cafe, owner));
			}
			
			@Nested
			class Join {
				CafeMember cafeMember;
				User member;
				
				@BeforeEach
				void create() {
					member = new_user("other", "1234");
					
					val form = new JoinCafeForm();
					form.setGreeting("hi");
					cafeMember = cafeService.joinCafe(cafe, member, form);
					em.flush();
				}
				
				@Test
				void shouldBeMemberAtThisCafe() {
					assertTrue(cafeService.isMember(cafe, member));
					assertTrue(cafeService.isJoined(cafe, member));
				}
				
				@Test
				void shouldExistsMember() {
					assertNotNull(cafeMember);
					assertEquals(cafeMember, cafeService.getCafeMemberById(cafeMember.getId()));
					assertTrue(cafeService.isMember(cafe, member));
					
					em.flush();
					em.clear();
					val load_cafe = cafeService.getCafeById(cafe.getId());
					
					assertEquals(1, load_cafe.getMemberCount());
					assertEquals(1, load_cafe.getMembers().size());
				}
				
				@Test
				void delete() {
					em.flush();
					cafeService.leaveCafe(cafe, member);
					assertFalse(cafeService.isMember(cafe, member));
					
					em.flush();
					em.clear();
					val load_cafe = cafeService.getCafeById(cafe.getId());
					assertEquals(0, load_cafe.getMemberCount());
					assertTrue(load_cafe.getMembers().isEmpty());
				}
				
				@Test
				void cascadeDelete() {
					em.flush();
					deleteCafe();
				}
			}
		}
		
		@Nested
		class WithPrivacyCafe {
			Cafe cafe;
			
			@BeforeEach
			void createCafe() {
				val form = new CafeForm();
				form.setDescription("this cafe is privacy");
				form.setId("cafe");
				form.setName("Privacy cafe");
				form.setNeedApproval(false);
				form.setPrivacy(true);
				
				cafe = cafeService.createCafe(form, owner);
			}
			
			@Test
			void shouldHaveEmptyList() {
				assertEquals(0, cafeService.countPublicCafeList());
				assertFalse(cafeService.getPublicCafeList().iterator().hasNext());
			}
			
			@Test
			void testProperties() {
				assertNotNull(cafe);
				assertTrue(cafe.isPrivacy());
			}
		}
		
		@Nested
		class WithApprovalCafe {
			Cafe cafe;
			
			@BeforeEach
			void createCafe() {
				val form = new CafeForm();
				form.setDescription("this cafe is approval");
				form.setId("cafe");
				form.setName("Approval cafe");
				form.setNeedApproval(true);
				form.setPrivacy(false);
				
				cafe = cafeService.createCafe(form, owner);
			}
			
			@Test
			void shouldHaveOne() {
				assertEquals(1, cafeService.countPublicCafeList());
				assertTrue(cafeService.getPublicCafeList().iterator().hasNext());
			}
			
			@Test
			void testProperties() {
				assertNotNull(cafe);
				assertTrue(cafe.isNeedApproval());
			}
		}
	}
}
