package com.andrey999r.staynova.domain.ports.in.commands;

public record UploadPhotoCommand(byte[] data, String fileName) {}
