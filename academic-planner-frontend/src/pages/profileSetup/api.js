import { authApi } from '../../axiosConfig';

export const addEvent = async (data) => {
  return await authApi.post('/event/fixed', data);
};
