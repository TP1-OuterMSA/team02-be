package com.example.community_cr.community.post_block.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.community_cr.community.post_block.entity.Block;
import com.example.community_cr.community.post_block.entity.BlockId;

public interface BlockRepository extends JpaRepository<Block, BlockId> {
}
