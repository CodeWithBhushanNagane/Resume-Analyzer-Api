// src/components/ResumeUpload/ResumeUpload.tsx
import React, { useState } from 'react';
import { Box, Button, CircularProgress, Typography } from '@mui/material';
import { useResumeUpload } from '../../hooks/UseResumeUpload';

const MAX_FILE_SIZE = 2 * 1024 * 1024; // 2MB
const ALLOWED_TYPES = [
  'application/pdf',
  'application/msword',
  'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
];

const ResumeUpload: React.FC = () => {
  const [file, setFile] = useState<File | null>(null);
  const { loading, message, upload, setMessage } = useResumeUpload();

  const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    if (e.target.files && e.target.files.length > 0) {
      const selectedFile = e.target.files[0];

      // Validate file type
      if (!ALLOWED_TYPES.includes(selectedFile.type)) {
        setMessage('Invalid file type. Only PDF and Word documents are allowed.');
        setFile(null);
        return;
      }

      // Validate file size
      if (selectedFile.size > MAX_FILE_SIZE) {
        setMessage('File is too large. Max size allowed is 2MB.');
        setFile(null);
        return;
      }

      setFile(selectedFile);
      setMessage('');
    }
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!file) {
      setMessage('Please select a file first.');
      return;
    }

    const success = await upload(file);
    if (success) {
      setFile(null);
    }
  };

  return (
    <Box
      maxWidth={400}
      mx="auto"
      mt={5}
      p={3}
      border={1}
      borderColor="grey.300"
      borderRadius={2}
      boxShadow={2}
    >
      <Typography variant="h5" mb={3} align="center">
        Upload Resume
      </Typography>

      <form onSubmit={handleSubmit}>
        <Button variant="contained" component="label" fullWidth disabled={loading}>
          {file ? file.name : 'Select Resume File'}
          <input
            hidden
            type="file"
            accept=".pdf,.doc,.docx"
            onChange={handleFileChange}
            disabled={loading}
          />
        </Button>

        {file && (
          <Typography variant="body2" mt={1} color="textSecondary">
            Selected file: {file.name} ({(file.size / 1024).toFixed(2)} KB)
          </Typography>
        )}

        <Box mt={3} textAlign="center">
          <Button
            variant="contained"
            color="primary"
            type="submit"
            disabled={!file || loading}
            fullWidth
          >
            {loading ? <CircularProgress size={24} /> : 'Upload'}
          </Button>
        </Box>
      </form>

      {message && (
        <Typography
          mt={3}
          align="center"
          color={message.startsWith('Error') || message.includes('failed') ? 'error' : 'primary'}
        >
          {message}
        </Typography>
      )}
    </Box>
  );
};

export default ResumeUpload;
