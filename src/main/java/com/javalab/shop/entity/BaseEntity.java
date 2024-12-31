package com.javalab.shop.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * 생성자, 수정자를 자동으로 관리하는 엔티티
 * BaseTimeEntity를 상속받아 생성자, 수정자를 자동으로 관리하는 엔티티를 만듦
 * 거기에 추가적으로 생성자, 수정자를 나타내는 필드를 추가함.
 * @EntityListeners(AuditingEntityListener.class)
 *  - JPA에게 해당 Entity는 Auditing 기능을 사용한다는 것을 알림.
 * @MappedSuperclass
 * - MappedSuperclass로 선언된 클래스는 엔티티가 아니므로 독립적으로 테이블에 만들지 말라는 의미이다.
 * - 이 어노테이션이 적용되어 있는 클래스를 상속하면 그 엔티티의 테이블 컬럼으로 만들어진다.
 * - Entity들이 BaseEntity를 상속할 경우 위의 필드들(createDate)을 자동으로 컬럼으로 인식하게됨.
 */
@EntityListeners({AuditingEntityListener.class})
@MappedSuperclass
@Getter@Setter
public abstract class BaseEntity extends BaseTimeEntity {

    // @CreatedBy : Entity가 생성되어 저장될 때 자동으로 작성자로 등록
    @CreatedBy
    @Column(updatable = false)
    private String createdBy;

    // @LastModifiedBy : Entity의 값을 변경할 때마다 자동으로 수정자로 등록
    @LastModifiedBy
    private String modifiedBy;
}
