ALTER TABLE resumes
    ADD COLUMN published TINYINT(1) NOT NULL DEFAULT 0 AFTER avatar_url,
    ADD KEY idx_resumes_published_updated (published, updated_at);
