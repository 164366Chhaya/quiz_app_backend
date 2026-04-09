package com.lets_quiz_it.backend.service;

import com.lets_quiz_it.backend.dto.TopicDTO;
import com.lets_quiz_it.backend.entity.Topic;
import com.lets_quiz_it.backend.repository.TopicRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TopicService {

    private final TopicRepository topicRepository;

    public List<Topic> getAllTopics() {
        return topicRepository.findAll();
    }

    public Topic createTopic(TopicDTO dto) {
        if (topicRepository.existsByNameIgnoreCase(dto.getName()))
            throw new RuntimeException("Topic already exists: " + dto.getName());

        return topicRepository.save(
                Topic.builder()
                        .name(dto.getName())
                        .subject(dto.getSubject())
                        .build()
        );
    }

    public Topic updateTopic(Long id, TopicDTO dto) {
        Topic topic = topicRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Topic not found"));
        topic.setName(dto.getName());
        topic.setSubject(dto.getSubject());
        return topicRepository.save(topic);
    }

    public void deleteTopic(Long id) {
        topicRepository.deleteById(id);
    }
}