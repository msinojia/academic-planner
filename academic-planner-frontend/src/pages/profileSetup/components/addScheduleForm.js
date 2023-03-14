import React, { useState } from 'react';
import { TimePicker, Modal, Form, Input, Radio, Row, Col, Select } from 'antd';
import { ArrayOfWeekDays, WeekDays } from '../contsants';

const AddScheduleForm = (props) => {
  const [form] = Form.useForm();
  const [isDaily, setIsDaily] = useState('no');

  const onSubmit = () => {
    let weeklyRepeatDays = [false, false, false, false, false, false, false];
    form
      .validateFields()
      .then((values) => {
        weeklyRepeatDays[values.weekDay] = true;
        values = {
          ...values,
          startDate: new Date().toISOString().slice(0, 10),
          endDate: '2023-12-31',
          startTime: `${values.startTime.hour()}:${values.startTime.minute()}`,
          endTime: `${values.endTime.hour()}:${values.endTime.minute()}`,
          isReschedulable: false,
          eventPriority: 'HIGH',
          isRepeat: true,
          repeatEvent: {
            repititionType: isDaily ? 'DAILY' : 'WEEKLY',
            weeklyRepeatDays: isDaily ? undefined : weeklyRepeatDays,
            endDate: '2023-12-31',
          },
          weekDay: isDaily ? ArrayOfWeekDays[values.weekDay] : undefined,
        };
        form.resetFields();
        props.handleOk(values);
      })
      .catch(() => {});
  };
  return (
    <Modal
      title={props.title}
      open={props.isModalOpen}
      onOk={onSubmit}
      okText={'Save'}
      onCancel={props.handleCancel}
    >
      <Form form={form}>
        <Form.Item
          name={'name'}
          label='Name'
          rules={[{ required: 'true', message: 'Enter Schedule Name!' }]}
        >
          <Input placeholder='Enter Name' />
        </Form.Item>
        <Form.Item label='Description' name={'description'}>
          <Input placeholder='Enter Description' />
        </Form.Item>
        <Row>
          <Col span={12}>
            <Form.Item
              label='Start Time'
              name={'startTime'}
              rules={[
                {
                  type: 'object',
                  required: 'true',
                  message: 'Select Start Time!',
                },
              ]}
            >
              <TimePicker format={'HH:mm'} />
            </Form.Item>
          </Col>
          <Col span={12}>
            <Form.Item
              label='End Time'
              name={'endTime'}
              rules={[
                {
                  type: 'object',
                  required: 'true',
                  message: 'Select End Time!',
                },
              ]}
            >
              <TimePicker format={'HH:mm'} />
            </Form.Item>
          </Col>
        </Row>
        <Form.Item label='Is it a daily schedule?' name='repeatType'>
          <Radio.Group
            defaultValue={isDaily}
            onChange={(event) => setIsDaily(event.target.value)}
          >
            <Radio.Button value='yes'>Yes</Radio.Button>
            <Radio.Button value='no'>No</Radio.Button>
          </Radio.Group>
        </Form.Item>
        {isDaily === 'no' && (
          <Form.Item
            name='weekDay'
            label='Day of Week'
            hasFeedback
            rules={[{ required: true, message: 'Please select a week day!' }]}
          >
            <Select placeholder='Select a week day' options={WeekDays} />
          </Form.Item>
        )}
      </Form>
    </Modal>
  );
};

export default AddScheduleForm;
