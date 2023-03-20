// import axios from '../../axiosConfig';
import { authApi } from '../../axiosConfig';

export const addEvent = async (data) => {
  return await authApi.post('/event', data);
};
