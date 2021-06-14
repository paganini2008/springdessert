### <code>fastjdbc-spring-boot-starter</code>
fastjdbc-spring-boot-starter, which is a <code>jdbc</code> rapid development kit based on the <code>SpringBoot</code> framework. It is actually a secondary encapsulation of the <code>NamedJdbcTemplate</code> provided by the spring framework, and provides an annotation-based API configuration method to operate SQL. At the same time, you can still use <code>spring's spring-boot-starter-data-jdbc</code> function

###  Compatibility
1. JDK 1.8 (or later)
2. <code>SpringBoot Framework 2.2.x </code>(or later)
3. <code>Redis 4.x</code> (or later)

### Install
``` xml
		<dependency>
			<groupId>com.github.paganini2008.springworld</groupId>
			<artifactId>fastjdbc-spring-boot-starter</artifactId>
			<version>2.0.1</version>
		</dependency>
```
### Example
``` java
@Dao
public interface UserDao {

	@Insert("insert into tb_user(username,password,age) values (:username,:password,:age)")
	int saveUser(@Example User user);

	@Update("update tb_user set username=:username, password=:password where id=:id")
	int updateUser(@Example User user);

	@Update("delete from tb_user where id=:id")
	int deleteUser(@Arg int id);
	
	@Batch("insert into tb_user(username,password,age) values (:username,:password,:age)")
	int batchSaveUser(@Args List<User> userList);

	@Get("select * from tb_user where id=:id")
	User getById(@Arg int id);
	
	@Query("select * from tb_user order by create_time desc")
	List<Map<String, Object>> queryUser();

	@Select("select * from tb_user order by create_time desc")
	ResultSetSlice<User> selectUser();

}
```
**Description:**
1. @Insert returns the primary key ID, which can be of type int or long
2. @Update returns the number of affected rows, it can execute insert, update, delete statements
3. @Batch returns the number of affected rows
4. @Get can return an object or a single value, just set the attribute <code>javaType=true</code>
5. @Query and @Select are very similar. @Query returns a list without pagination. @Select supports both pagination and lists. It returns a <code>ResultSetSlice</code> object. This object is extremely powerful. Friends who are interested can study it.
6. The <code>sql</code> statement is written in the same way as the <code>NamedParameterJdbcTemplate</code> in the Spring framework, and the <code>sql</code> statement is actually executed through it.

   Finally, in your <code>SpringBoot</code> application, add Dao scanner to your own Configuration class.
``` java
@DaoScan(basePackages = "com.yourcompany.project.base.dao")
@Configuration(proxyBeanMethods = false)
public class YourConfiguration {

}
```
