export interface ApiResponse {
    status: string;
    fileName?: string;
    message?: string;
  }
  
  export async function uploadResumeFile(file: File): Promise<ApiResponse> {
    const formData = new FormData();
    formData.append('file', file);
  
    const response = await fetch('http://localhost:8080/api/resume/upload', {
      method: 'POST',
      body: formData,
    });
  
    if (!response.ok) {
      throw new Error(`Upload failed with status ${response.status}`);
    }
  
    return response.json();
  }
  