import { useState } from 'react';
import { uploadResumeFile, ApiResponse } from '../api/ResumeApi';

export function useResumeUpload() {
    const [loading, setLoading] = useState(false);
    const [message, setMessage] = useState<string>('');
  
    const upload = async (file: File | null): Promise<boolean> => {
      if (!file) {
        setMessage('No file selected.');
        return false;
      }
  
      setLoading(true);
      setMessage('');
  
      try {
        const data: ApiResponse = await uploadResumeFile(file);
  
        if (data.status === 'success') {
          setMessage(`Upload successful: ${data.fileName}`);
          return true;
        } else {
          setMessage(data.message || 'Upload failed');
          return false;
        }
      } catch (error: any) {
        setMessage(`Error: ${error.message}`);
        return false;
      } finally {
        setLoading(false);
      }
    };
  
    return {
      loading,
      message,
      upload,
      setMessage,
    };
  }