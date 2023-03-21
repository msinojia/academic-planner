import React, { useState } from 'react';
import { TimePicker, Modal, Form, Input, Row, Col, DatePicker } from 'antd';

const VariableEventModal = (props) => {
  const [form] = Form.useForm();

  const onSubmit = () => {
    form
      .validateFields()
      .then((values) => {})
      .catch((err) => {
        console.log(err);
      });
  };
  // WeekDays;
  return (
    <Modal
      title={props.isAdd ? 'Add new schedule' : 'Edit existing schedule'}
      open={props.isModalOpen}
      onOk={onSubmit}
      okText={'Save'}
      onCancel={props.handleCancel}
    >
      <Form form={form} initialValues={props.data}>
        <Form.Item
          name={'name'}
          label='Name'
          rules={[{ required: 'true', message: 'Enter Schedule Name!' }]}
        >
          <Input placeholder='Enter Name' />
        </Form.Item>
        <Form.Item
          name='eventDate'
          label='Date'
          hasFeedback
          rules={[{ required: true, message: 'Please select a day!' }]}
        >
          <DatePicker />
        </Form.Item>
        {/* <Form.Item label='Description' name={'details'}>
          <Input placeholder='Enter Description' />
        </Form.Item> */}
        {/* {props.isLectLab && (
          <Form.Item
            label='Class Type'
            name='eventCategory'
            rules={[
              {
                required: 'true',
                message: 'Select Class Type!',
              },
            ]}
          >
            <Radio.Group>
              <Radio.Button value='CLASS'>Lecture</Radio.Button>
              <Radio.Button value='LAB'>Laboratory</Radio.Button>
            </Radio.Group>
          </Form.Item>
        )} */}
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
      </Form>
    </Modal>
  );
};

export default VariableEventModal;
