package com.edi.infrastructure.repository;

import com.edi.domain.model.board.BoardMember;
import com.edi.domain.model.board.BoardMemberRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;

@Repository
public class HibernateBoardMemberRepository extends HibernateSupport<BoardMember> implements BoardMemberRepository {

  @Autowired
  HibernateBoardMemberRepository(@Qualifier("masterEntityManager") EntityManager entityManager) {
    super(entityManager);
  }
}
